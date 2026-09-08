#!/usr/bin/env bash
#
# Bump the `selenium.version` and `spring.boot.version` Maven properties in the
# root pom.xml of one branch, and open -- or update -- a rolling pull request
# with the change.
#
# The two dependencies get deliberately different policies, because they
# release differently:
#
#   selenium.version    Newest stable release on the same MAJOR line.
#                       Selenium ships a new *minor* for every release
#                       (4.44.0, 4.45.0, ... 4.48.0) and treats them as
#                       ordinary updates, so crossing a minor is in scope and
#                       a lagging branch catches up in a single PR. Crossing a
#                       major (4.x -> 5.x) never is.
#
#   spring.boot.version Newest stable release on the same MAJOR.MINOR line.
#                       A minor change alters what a maintenance branch ships
#                       and needs human review, so this is patch-only. One
#                       exception: a branch pinned to a prerelease of its own
#                       line (main and 25.2 sit on 4.1.0-RC1) is allowed to
#                       move up to a stable release on that same line, since
#                       that unpins a milestone without changing the line.
#
# Both anchor on the branch's own current value rather than on the metadata's
# <latest>/<release> fields. Those fields cannot be trusted here:
# spring-boot-starter-parent currently reports a milestone of a newer minor,
# which would drag every maintenance branch onto a prerelease of the wrong
# line.
#
# The pull request is a *rolling* one: a run reuses this job's own open PR for
# the branch and resets it to base, so the PR stays a single commit instead of
# accumulating one bump commit per day. It is identified by its head branch
# prefix (bot/bump-dependencies-*), not by its title, so it never hijacks a
# human's PR.
#
# Usage:
#   bump-dependencies.sh --branch <branch> [options]
#
#   --branch <branch>     Base branch to bump, e.g. main, 25.2, 8.3. Required.
#   --repo-dir <dir>      Checkout to operate on. Defaults to this script's own
#                         repository, which is only allowed with --dry-run
#                         (see the guard in resolve_repo_dir).
#   --repo <owner/name>   Repo slug for `gh`. Default: derived from origin.
#   --reviewers <a,b>     Reviewers to request. Default: ZheSun88.
#   --result-file <file>  Write a JSON summary here, even on failure.
#   --dry-run             Resolve versions and report only. No branch, no PR.
#   --self-test           Run the built-in assertions and exit.
#   --verbose             Extra logging.
#
# Requires: bash 4+, curl, git, jq, and (unless --dry-run) an authenticated gh.

set -euo pipefail

readonly POM_PATH="pom.xml"
readonly BOT_BRANCH_PREFIX="bot/bump-dependencies"
readonly DEFAULT_REVIEWERS="ZheSun88"
readonly LABELS=(dependencies automated)

readonly SELENIUM_PROPERTY="selenium.version"
readonly SPRING_BOOT_PROPERTY="spring.boot.version"

readonly SELENIUM_METADATA_URL="https://repo.maven.apache.org/maven2/org/seleniumhq/selenium/selenium-java/maven-metadata.xml"
readonly SPRING_BOOT_METADATA_URL="https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-parent/maven-metadata.xml"
readonly USER_AGENT="vaadin-testbench-bump-dependencies/1.0"

readonly WORKFLOW_URL="https://github.com/vaadin/testbench/actions/workflows/bump-dependencies.yml"

# ---------------------------------------------------------------------------
# Logging
# ---------------------------------------------------------------------------

VERBOSE=false

log()  { printf '%s\n' "$*" >&2; }
warn() { printf 'warning: %s\n' "$*" >&2; }

debug() {
    if [[ $VERBOSE == true ]]; then
        printf '  %s\n' "$*" >&2
    fi
}

# The message is recorded for the result file by the EXIT trap, so a failed run
# still contributes an error row to the workflow report instead of vanishing.
die() {
    RESULT_ERROR="$*"
    printf 'error: %s\n' "$*" >&2
    exit 1
}

trim() {
    local s="$1"
    s="${s#"${s%%[![:space:]]*}"}"
    s="${s%"${s##*[![:space:]]}"}"
    printf '%s' "$s"
}

# ---------------------------------------------------------------------------
# Version comparison
#
# Hand-rolled rather than delegating to `sort -V`, whose treatment of the
# hyphen in `4.1.0-RC1` versus `4.1.0` is not something worth betting a
# force-push on.
# ---------------------------------------------------------------------------

# Print "<major> <minor> <patch> [prerelease]", or fail for anything that is
# not a strict three-component version. Failing is the point: Selenium's
# metadata still lists relics like `2.0b1` and `3.141.59`, and those must be
# skipped rather than guessed at.
ver_parse() {
    local v="$1"
    [[ $v =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)(-(.+))?$ ]] || return 1
    printf '%s %s %s %s' \
        "${BASH_REMATCH[1]}" "${BASH_REMATCH[2]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[5]-}"
}

ver_is_stable() {
    local parsed pre
    parsed=$(ver_parse "$1") || return 1
    read -r _ _ _ pre <<<"$parsed"
    [[ -z ${pre:-} ]]
}

# Print -1, 0 or 1 for a<b, a==b, a>b. A stable release outranks any
# prerelease of the same numeric version, which is what lets 4.1.0-RC1 -> 4.1.1
# register as an upgrade.
ver_cmp() {
    local pa pb amaj amin apat apre bmaj bmin bpat bpre pair a b
    pa=$(ver_parse "$1") || die "not a parseable version: $1"
    pb=$(ver_parse "$2") || die "not a parseable version: $2"
    read -r amaj amin apat apre <<<"$pa"
    read -r bmaj bmin bpat bpre <<<"$pb"
    apre=${apre:-}
    bpre=${bpre:-}

    for pair in "$amaj:$bmaj" "$amin:$bmin" "$apat:$bpat"; do
        a=${pair%%:*}
        b=${pair##*:}
        if [[ $a -lt $b ]]; then printf '%s' -1; return 0; fi
        if [[ $a -gt $b ]]; then printf '%s' 1; return 0; fi
    done

    if [[ -z $apre && -n $bpre ]]; then printf '%s' 1; return 0; fi
    if [[ -n $apre && -z $bpre ]]; then printf '%s' -1; return 0; fi
    if [[ $apre > $bpre ]]; then printf '%s' 1; return 0; fi
    if [[ $apre < $bpre ]]; then printf '%s' -1; return 0; fi
    printf '%s' 0
}

# Newest stable candidate within `scope` of `current`, or nothing when already
# up to date. `scope` is `major` (selenium) or `majorminor` (spring boot).
pick_latest() {
    local current="$1" scope="$2"
    shift 2
    local cmaj cmin parsed best="" v vmaj vmin vpre
    parsed=$(ver_parse "$current") || die "current version is not parseable: $current"
    read -r cmaj cmin _ _ <<<"$parsed"

    for v in "$@"; do
        parsed=$(ver_parse "$v") || continue
        read -r vmaj vmin _ vpre <<<"$parsed"
        if [[ -n ${vpre:-} ]]; then continue; fi
        if [[ $vmaj -ne $cmaj ]]; then continue; fi
        if [[ $scope == majorminor && $vmin -ne $cmin ]]; then continue; fi
        if [[ -z $best ]] || [[ $(ver_cmp "$v" "$best") == 1 ]]; then
            best="$v"
        fi
    done

    if [[ -n $best && $(ver_cmp "$best" "$current") == 1 ]]; then
        printf '%s\n' "$best"
    fi
    return 0
}

# Whether a stable release exists beyond what pick_latest will take. Purely
# informational -- it goes in the PR body so a reviewer knows a bigger upgrade
# is waiting, without this job ever attempting it.
has_newer_out_of_scope() {
    local current="$1" scope="$2"
    shift 2
    local cmaj cmin parsed v vmaj vmin vpre
    parsed=$(ver_parse "$current") || return 1
    read -r cmaj cmin _ _ <<<"$parsed"

    for v in "$@"; do
        parsed=$(ver_parse "$v") || continue
        read -r vmaj vmin _ vpre <<<"$parsed"
        if [[ -n ${vpre:-} ]]; then continue; fi
        if [[ $vmaj -gt $cmaj ]]; then return 0; fi
        if [[ $scope == majorminor && $vmaj -eq $cmaj && $vmin -gt $cmin ]]; then return 0; fi
    done
    return 1
}

# ---------------------------------------------------------------------------
# Maven Central
# ---------------------------------------------------------------------------

# The workflow fans out one process per branch, so half a dozen requests hit
# Maven Central at once; a single transient failure should not turn an
# otherwise-fine branch into a red job. `Connection: close` keeps curl from
# holding a pooled socket open for the one request this makes.
fetch_versions() {
    local url="$1" attempt body versions backoff
    for attempt in 1 2 3; do
        if body=$(curl -sSfL --max-time 60 -A "$USER_AGENT" -H 'Connection: close' "$url" 2>&1); then
            versions=$({ printf '%s\n' "$body" | grep -o '<version>[^<]*</version>' || true; } |
                sed 's|</\?version>||g')
            if [[ -n $versions ]]; then
                printf '%s\n' "$versions"
                return 0
            fi
            # A 200 with no <version> elements means the metadata moved or is
            # malformed. Treating that as "nothing newer" would silently stop
            # bumping forever, so it is a hard failure instead.
            body="fetched $url but found no <version> elements"
        fi
        if [[ $attempt -lt 3 ]]; then
            backoff=$((500 * 2 ** (attempt - 1)))
            warn "fetching $url failed (attempt $attempt/3), retrying in ${backoff}ms: ${body%%$'\n'*}"
            sleep "$(awk "BEGIN { print $backoff / 1000 }")"
        fi
    done
    die "failed to fetch $url after 3 attempts: ${body%%$'\n'*}"
}

# ---------------------------------------------------------------------------
# pom.xml property access
#
# A targeted string replace, not an XML round-trip: re-emitting the document
# would reformat the whole pom and turn a one-line bump into an unreviewable
# diff.
# ---------------------------------------------------------------------------

prop_regex() {
    printf '%s' "${1//./\\.}"
}

prop_count() {
    local file="$1" esc
    esc=$(prop_regex "$2")
    { grep -o "<$esc>" "$file" || true; } | wc -l | tr -d '[:space:]'
}

prop_read() {
    local file="$1" esc raw
    esc=$(prop_regex "$2")
    raw=$(sed -n "s|.*<$esc>\([^<]*\)</$esc>.*|\1|p" "$file" | head -1)
    trim "$raw"
}

# Both properties are declared exactly once each, in the root pom, with every
# other reference being ${...}. That invariant is what makes a one-line replace
# safe, so callers assert it rather than assume it -- silently editing one of
# two declarations, or none at all, would be far more expensive to discover
# later.
prop_replace() {
    local file="$1" prop="$2" next="$3" esc tmp
    esc=$(prop_regex "$prop")
    tmp="$file.bump.tmp"
    sed "s|\(<$esc>\)[^<]*\(</$esc>\)|\1$next\2|" "$file" >"$tmp"
    if cmp -s "$file" "$tmp"; then
        rm -f "$tmp"
        die "replacing <$prop> with $next in $file changed nothing"
    fi
    mv "$tmp" "$file"
}

# ---------------------------------------------------------------------------
# git / gh
# ---------------------------------------------------------------------------

REPO_DIR=""
REPO_SLUG=""

git_r() { git -C "$REPO_DIR" "$@"; }

require_cmd() {
    command -v "$1" >/dev/null 2>&1 || die "required command not found: $1"
}

require_gh_auth() {
    require_cmd gh
    gh auth status >/dev/null 2>&1 ||
        die "gh is not authenticated. Set GH_TOKEN, or run 'gh auth login'."
}

derive_slug() {
    local url
    url=$(git_r remote get-url origin) || die "no 'origin' remote in $REPO_DIR"
    url=${url%.git}
    url=${url%/}
    # Accepts both https://github.com/owner/name and git@github.com:owner/name
    [[ $url =~ ([^/:]+)/([^/]+)$ ]] ||
        die "could not derive an owner/name slug from the origin URL: $url"
    printf '%s/%s' "${BASH_REMATCH[1]}" "${BASH_REMATCH[2]}"
}

# A real bump must see the true tip of the base: branching from a stale
# origin/<base> would push a commit that silently reverts whatever landed
# since. A dry run only reads, so it may fall back to a possibly-stale
# remote-tracking ref, which lets it work offline.
fetch_base() {
    local branch="$1" dry_run="$2"
    if git_r fetch --quiet origin "$branch"; then
        return 0
    fi
    if [[ $dry_run == true ]] &&
        git_r rev-parse --verify --quiet "refs/remotes/origin/$branch" >/dev/null; then
        warn "could not reach origin, using local origin/$branch which may be stale"
        return 0
    fi
    die "could not fetch origin/$branch"
}

find_existing_pr() {
    local branch="$1"
    # Matched on head-branch prefix rather than on the title: the title carries
    # the version numbers and so changes every run, and matching titles risks
    # adopting an unrelated human PR and force-pushing over it.
    gh pr list -R "$REPO_SLUG" --base "$branch" --state open \
        --json number,headRefName,url --limit 100 |
        jq -c --arg prefix "$BOT_BRANCH_PREFIX-" \
            'map(select(.headRefName | startswith($prefix))) | first // empty'
}

find_unique_branch_name() {
    local base="$1" candidate i
    for i in $(seq 1 99); do
        candidate="$base"
        if [[ $i -gt 1 ]]; then candidate="$base-$i"; fi
        if git_r show-ref --verify --quiet "refs/heads/$candidate"; then continue; fi
        if git_r ls-remote --exit-code --heads origin "$candidate" >/dev/null 2>&1; then continue; fi
        printf '%s\n' "$candidate"
        return 0
    done
    die "could not find an unused branch name based on $base"
}

# Point the bot branch back at origin/<base>, discarding whatever the previous
# run put there. This is what keeps the rolling PR at a single commit.
reset_branch_to_base() {
    local branch="$1" base="$2"
    git_r fetch --quiet origin "$branch:refs/remotes/origin/$branch" || true
    # Detach first so `branch -D` can delete the branch even when it is the one
    # currently checked out.
    git_r checkout --quiet --detach "origin/$base" || true
    git_r branch -D "$branch" >/dev/null 2>&1 || true
    git_r checkout --quiet -b "$branch" "origin/$base"
}

# Labels are applied one at a time, after the PR exists, rather than passed to
# `gh pr create`. That flag is all-or-nothing: `automated` does not exist in
# this repo, so handing both to `pr create` failed the call outright and the
# retry-without-labels path then cost the PR its `dependencies` label too --
# which is exactly how #2298 came out with no labels at all.
apply_labels() {
    local pr_number="$1" label
    for label in "${LABELS[@]}"; do
        if ! gh pr edit "$pr_number" -R "$REPO_SLUG" --add-label "$label" >/dev/null 2>&1; then
            warn "could not add the '$label' label to #$pr_number; it may not exist in $REPO_SLUG"
        fi
    done
}

request_reviewers() {
    local pr_number="$1" reviewers="$2"
    if [[ -z $reviewers ]]; then return 0; fi
    # A review request can legitimately fail -- the account may not be a
    # collaborator, or may be the very identity that opened the PR. The PR body
    # mentions the reviewers too, so the ping still lands; losing the whole
    # bump over it would not be a trade worth making.
    if ! gh pr edit "$pr_number" -R "$REPO_SLUG" --add-reviewer "$reviewers" >/dev/null 2>&1; then
        warn "could not request a review from $reviewers on #$pr_number; the PR body still mentions them"
    fi
}

# ---------------------------------------------------------------------------
# Result state
# ---------------------------------------------------------------------------

RESULT_FILE=""
RESULT_BRANCH=""
RESULT_STATUS="error"
RESULT_ERROR=""
RESULT_PR_NUMBER=""
RESULT_PR_URL=""

SELENIUM_CURRENT=""
SELENIUM_NEXT=""
SELENIUM_OUT_OF_SCOPE=""
SPRING_BOOT_CURRENT=""
SPRING_BOOT_NEXT=""
SPRING_BOOT_OUT_OF_SCOPE=""

json_str() {
    if [[ -z ${1:-} ]]; then
        printf 'null'
    else
        printf '%s' "$1" | jq -Rs .
    fi
}

write_result() {
    if [[ -z $RESULT_FILE ]]; then return 0; fi
    mkdir -p "$(dirname "$RESULT_FILE")"
    {
        printf '{\n'
        printf '  "branch": %s,\n' "$(json_str "$RESULT_BRANCH")"
        printf '  "status": %s,\n' "$(json_str "$RESULT_STATUS")"
        printf '  "selenium": { "from": %s, "to": %s },\n' \
            "$(json_str "$SELENIUM_CURRENT")" "$(json_str "$SELENIUM_NEXT")"
        printf '  "springBoot": { "from": %s, "to": %s },\n' \
            "$(json_str "$SPRING_BOOT_CURRENT")" "$(json_str "$SPRING_BOOT_NEXT")"
        if [[ -n $RESULT_PR_NUMBER ]]; then
            printf '  "pr": { "number": %s, "url": %s },\n' \
                "$RESULT_PR_NUMBER" "$(json_str "$RESULT_PR_URL")"
        else
            printf '  "pr": null,\n'
        fi
        printf '  "error": %s\n' "$(json_str "$RESULT_ERROR")"
        printf '}\n'
    } >"$RESULT_FILE"
}

on_exit() {
    local code=$?
    if [[ $code -ne 0 ]]; then
        RESULT_STATUS="error"
        if [[ -z $RESULT_ERROR ]]; then
            RESULT_ERROR="the bump exited with status $code"
        fi
    fi
    write_result
    return $code
}

# ---------------------------------------------------------------------------
# PR content
# ---------------------------------------------------------------------------

join_and() {
    case $# in
        0) printf '' ;;
        1) printf '%s' "$1" ;;
        2) printf '%s and %s' "$1" "$2" ;;
        *) printf '%s, %s' "$1" "$(shift; join_and "$@")" ;;
    esac
}

commit_title() {
    local branch="$1"
    shift
    printf 'chore: bump %s [%s]' "$(join_and "$@")" "$branch"
}

pr_body() {
    local branch="$1" reviewers="$2" r
    printf 'Updates the dependency versions declared in `%s` on `%s`.\n\n' "$POM_PATH" "$branch"
    printf '| property | from | to | policy |\n|---|---|---|---|\n'
    if [[ -n $SELENIUM_NEXT ]]; then
        printf '| `%s` | %s | **%s** | newest stable on the %s.x line |\n' \
            "$SELENIUM_PROPERTY" "$SELENIUM_CURRENT" "$SELENIUM_NEXT" "${SELENIUM_CURRENT%%.*}"
    fi
    if [[ -n $SPRING_BOOT_NEXT ]]; then
        printf '| `%s` | %s | **%s** | newest stable patch on the %s line |\n' \
            "$SPRING_BOOT_PROPERTY" "$SPRING_BOOT_CURRENT" "$SPRING_BOOT_NEXT" \
            "$(printf '%s' "$SPRING_BOOT_CURRENT" | cut -d. -f1,2)"
    fi
    # Only the policies and sources actually in play, so a selenium-only PR on
    # a branch with no spring.boot.version does not explain a rule it never
    # applied.
    printf '\n'
    if [[ -n $SELENIUM_NEXT ]]; then
        printf 'Selenium may cross a minor, because Selenium ships a new minor for every\n'
        printf 'release and treats them as ordinary updates. It never crosses a major.\n'
    fi
    if [[ -n $SPRING_BOOT_NEXT ]]; then
        printf 'Spring Boot is patch-only: a minor changes what this branch ships, so it\n'
        printf 'needs human review rather than an automated PR.\n'
    fi
    printf '\n'
    if [[ -n $SELENIUM_NEXT ]]; then
        printf -- '- selenium source of truth: [selenium-java](%s)\n' "$SELENIUM_METADATA_URL"
    fi
    if [[ -n $SPRING_BOOT_NEXT ]]; then
        printf -- '- Spring Boot source of truth: [spring-boot-starter-parent](%s)\n' \
            "$SPRING_BOOT_METADATA_URL"
    fi

    # Gated on the dependency actually moving: an out-of-scope note about a
    # property this PR does not touch is noise on every future run.
    local selenium_note="" spring_note=""
    if [[ -n $SELENIUM_NEXT && -n $SELENIUM_OUT_OF_SCOPE ]]; then selenium_note=yes; fi
    if [[ -n $SPRING_BOOT_NEXT && -n $SPRING_BOOT_OUT_OF_SCOPE ]]; then spring_note=yes; fi
    if [[ -n $selenium_note || -n $spring_note ]]; then
        printf '\n> [!NOTE]\n'
        if [[ -n $selenium_note ]]; then
            printf '> A newer Selenium major also exists. This PR deliberately does not include it.\n'
        fi
        if [[ -n $spring_note ]]; then
            printf '> A newer Spring Boot major/minor also exists. This PR deliberately does not include it.\n'
        fi
    fi

    if [[ -n $reviewers ]]; then
        printf '\ncc'
        for r in ${reviewers//,/ }; do printf ' @%s' "$r"; done
        printf ' for review.\n'
    fi

    printf '\n---\nOpened by the [`bump-dependencies`](%s) workflow. ' "$WORKFLOW_URL"
    printf 'It force-pushes this branch, so push fixups elsewhere.\n'
}

# ---------------------------------------------------------------------------
# Self test
# ---------------------------------------------------------------------------

TESTS_RUN=0
TESTS_FAILED=0

expect_eq() {
    local actual="$1" expected="$2" what="$3"
    TESTS_RUN=$((TESTS_RUN + 1))
    if [[ $actual == "$expected" ]]; then
        debug "ok: $what"
    else
        TESTS_FAILED=$((TESTS_FAILED + 1))
        log "FAIL: $what -- expected '$expected', got '$actual'"
    fi
}

# Asserts a function that signals through its exit status. The description
# comes first so the command and its arguments pass through as "$@" --
# collapsing them into a single string would have expect_true look for a
# command literally named "ver_is_stable 4.48.0".
expect_true() {
    local what="$1"
    shift
    if "$@" >/dev/null 2>&1; then
        expect_eq true true "$what"
    else
        expect_eq false true "$what"
    fi
}

expect_false() {
    local what="$1"
    shift
    if "$@" >/dev/null 2>&1; then
        expect_eq true false "$what"
    else
        expect_eq false false "$what"
    fi
}

self_test() {
    # Selenium's real published list, trimmed, plus a hypothetical 5.x to prove
    # the major boundary holds, and two relics that must be skipped.
    local -a selenium=(2.0b1 3.141.59 4.44.0 4.45.0 4.46.0 4.47.0 4.48.0 5.0.0 5.1.0)
    # Spring Boot's real list around the lines these branches sit on.
    local -a spring=(3.5.0 3.5.6 3.5.9 4.0.4 4.0.8 4.1.0-M4 4.1.0-RC1 4.1.0 4.1.1 4.2.0-M1)

    expect_eq "$(ver_cmp 4.48.0 4.44.0)" 1 "4.48.0 > 4.44.0"
    expect_eq "$(ver_cmp 4.44.0 4.48.0)" -1 "4.44.0 < 4.48.0"
    expect_eq "$(ver_cmp 3.5.0 3.5.0)" 0 "3.5.0 == 3.5.0"
    expect_eq "$(ver_cmp 4.1.0 4.1.0-RC1)" 1 "GA outranks its own RC"
    expect_eq "$(ver_cmp 4.1.0-RC1 4.1.0)" -1 "RC ranks below its own GA"
    expect_eq "$(ver_cmp 4.10.0 4.9.0)" 1 "minor compares numerically, not lexically"
    expect_eq "$(ver_cmp 4.1.1 4.1.0-RC1)" 1 "later patch outranks an earlier RC"

    expect_true "4.48.0 is stable" ver_is_stable 4.48.0
    expect_false "4.1.0-RC1 is a prerelease" ver_is_stable 4.1.0-RC1
    expect_false "2.0b1 is not a parseable version" ver_parse 2.0b1
    expect_false "a two-component version is not parseable" ver_parse 3.141

    # Selenium: same major, minor crossing allowed, one PR to catch up.
    expect_eq "$(pick_latest 4.44.0 major "${selenium[@]}")" 4.48.0 \
        "selenium 4.44.0 catches up to 4.48.0"
    expect_eq "$(pick_latest 4.46.0 major "${selenium[@]}")" 4.48.0 \
        "selenium 4.46.0 catches up to 4.48.0"
    expect_eq "$(pick_latest 4.48.0 major "${selenium[@]}")" "" \
        "selenium 4.48.0 is already current"
    expect_eq "$(pick_latest 5.0.0 major "${selenium[@]}")" 5.1.0 \
        "selenium stays inside its own major"

    # Spring Boot: same major.minor only.
    expect_eq "$(pick_latest 4.0.4 majorminor "${spring[@]}")" 4.0.8 \
        "spring boot 4.0.4 -> 4.0.8, not 4.1.x"
    expect_eq "$(pick_latest 3.5.0 majorminor "${spring[@]}")" 3.5.9 \
        "spring boot 3.5.0 -> 3.5.9, not 4.x"
    expect_eq "$(pick_latest 4.1.0-RC1 majorminor "${spring[@]}")" 4.1.1 \
        "spring boot RC pin moves up to GA on its own line"
    expect_eq "$(pick_latest 4.1.1 majorminor "${spring[@]}")" "" \
        "spring boot 4.1.1 is already current"
    expect_eq "$(pick_latest 4.0.8 majorminor "${spring[@]}")" "" \
        "spring boot never crosses a minor to catch up"

    expect_true "a newer selenium major is reported" \
        has_newer_out_of_scope 4.44.0 major "${selenium[@]}"
    expect_false "nothing exists beyond selenium 5.1.0" \
        has_newer_out_of_scope 5.1.0 major "${selenium[@]}"
    expect_true "a newer spring boot minor is reported" \
        has_newer_out_of_scope 4.0.8 majorminor "${spring[@]}"
    expect_false "a 4.2.0 milestone is not reported as a newer stable minor" \
        has_newer_out_of_scope 4.1.1 majorminor "${spring[@]}"

    # Property access, against the real shape of this repo's root pom.
    local fixture
    fixture=$(mktemp)
    cat >"$fixture" <<'FIXTURE'
    <properties>
        <selenium.version>4.48.0</selenium.version>
        <spring.boot.version>4.1.0-RC1</spring.boot.version>
    </properties>
    <dependency>
        <version>${selenium.version}</version>
    </dependency>
FIXTURE

    expect_eq "$(prop_count "$fixture" "$SELENIUM_PROPERTY")" 1 "selenium is declared once"
    expect_eq "$(prop_count "$fixture" "$SPRING_BOOT_PROPERTY")" 1 "spring boot is declared once"
    expect_eq "$(prop_count "$fixture" "no.such.version")" 0 "an absent property counts zero"
    expect_eq "$(prop_read "$fixture" "$SELENIUM_PROPERTY")" 4.48.0 "selenium value is read"
    expect_eq "$(prop_read "$fixture" "$SPRING_BOOT_PROPERTY")" 4.1.0-RC1 "spring boot value is read"

    prop_replace "$fixture" "$SELENIUM_PROPERTY" 4.49.0
    expect_eq "$(prop_read "$fixture" "$SELENIUM_PROPERTY")" 4.49.0 "selenium value is replaced"
    expect_eq "$(grep -c 'selenium\.version}' "$fixture")" 1 \
        'the property reference is left alone'
    expect_eq "$(prop_read "$fixture" "$SPRING_BOOT_PROPERTY")" 4.1.0-RC1 \
        "the other property is left alone"
    rm -f "$fixture"

    expect_eq "$(commit_title main 'selenium to 4.48.0' 'Spring Boot to 4.1.1')" \
        "chore: bump selenium to 4.48.0 and Spring Boot to 4.1.1 [main]" \
        "commit title names both dependencies"
    expect_eq "$(commit_title 8.3 'selenium to 4.48.0')" \
        "chore: bump selenium to 4.48.0 [8.3]" \
        "commit title names the one dependency that moved"

    log ""
    if [[ $TESTS_FAILED -eq 0 ]]; then
        log "self-test: $TESTS_RUN assertions passed"
        return 0
    fi
    log "self-test: $TESTS_FAILED of $TESTS_RUN assertions failed"
    return 1
}

# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

usage() {
    sed -n '2,/^set -euo/p' "$0" | sed 's/^#\{0,1\} \{0,1\}//; $d' >&2
    exit "${1:-2}"
}

resolve_repo_dir() {
    local requested="$1" dry_run="$2" script_root

    # Both paths are normalized through `git rev-parse --show-toplevel` so the
    # comparison below is between two strings in git's own notation. Mixing in
    # a shell `pwd` breaks it on Windows, where git-bash reports
    # /c/WorkFolder/testbench and git reports C:/WorkFolder/testbench -- the
    # guard then silently never fires.
    if [[ -n $requested ]]; then
        [[ -d $requested ]] || die "no such directory: $requested"
        REPO_DIR=$(git -C "$requested" rev-parse --show-toplevel) ||
            die "not a git checkout: $requested"
    else
        REPO_DIR=$(git -C "$(dirname "$0")" rev-parse --show-toplevel) ||
            die "--repo-dir was not given and this script is not inside a git checkout"
    fi

    # Checking out a release branch replaces the working tree -- and this
    # script does not exist on branches like 8.3, so a real run against its own
    # checkout would delete the file bash is still reading. Refuse up front
    # rather than fail halfway through in a way that is baffling to debug.
    if [[ $dry_run != true ]]; then
        script_root=$(git -C "$(dirname "$0")" rev-parse --show-toplevel 2>/dev/null) || script_root=""
        if [[ -n $script_root && $script_root == "$REPO_DIR" ]]; then
            die "refusing to bump the checkout this script lives in: checking out the target branch would delete the script mid-run. Pass --repo-dir pointing at a separate clone or worktree, or use --dry-run."
        fi
    fi
}

main() {
    local branch="" repo_dir_arg="" slug_arg="" reviewers="$DEFAULT_REVIEWERS"
    local dry_run=false run_self_test=false

    while [[ $# -gt 0 ]]; do
        case "$1" in
            --branch) branch="${2:-}"; shift 2 ;;
            --repo-dir) repo_dir_arg="${2:-}"; shift 2 ;;
            --repo) slug_arg="${2:-}"; shift 2 ;;
            --reviewers) reviewers="${2:-}"; shift 2 ;;
            --result-file) RESULT_FILE="${2:-}"; shift 2 ;;
            --dry-run) dry_run=true; shift ;;
            --self-test) run_self_test=true; shift ;;
            --verbose) VERBOSE=true; shift ;;
            -h|--help) usage 0 ;;
            *) log "unknown option: $1"; usage 2 ;;
        esac
    done

    if [[ $run_self_test == true ]]; then
        require_cmd jq
        self_test
        return $?
    fi

    if [[ -z $branch ]]; then
        log "missing required option --branch"
        usage 2
    fi
    RESULT_BRANCH="$branch"

    require_cmd curl
    require_cmd git
    require_cmd jq

    resolve_repo_dir "$repo_dir_arg" "$dry_run"
    REPO_SLUG="${slug_arg:-$(derive_slug)}"
    debug "repo $REPO_SLUG at $REPO_DIR"

    fetch_base "$branch" "$dry_run"

    # Read the pom as of the base ref without checking it out, so the version
    # resolution below reports on the branch even when no bump follows.
    local work pom
    work=$(mktemp -d)
    pom="$work/pom.xml"
    git_r show "origin/$branch:$POM_PATH" >"$pom" ||
        die "could not read $POM_PATH from origin/$branch"

    local selenium_count spring_count
    selenium_count=$(prop_count "$pom" "$SELENIUM_PROPERTY")
    spring_count=$(prop_count "$pom" "$SPRING_BOOT_PROPERTY")

    [[ $selenium_count -eq 1 ]] ||
        die "expected exactly one <$SELENIUM_PROPERTY> in $branch:$POM_PATH, found $selenium_count"
    [[ $spring_count -le 1 ]] ||
        die "expected at most one <$SPRING_BOOT_PROPERTY> in $branch:$POM_PATH, found $spring_count. Refusing to guess which one to bump."

    local -a selenium_versions=() spring_versions=()

    SELENIUM_CURRENT=$(prop_read "$pom" "$SELENIUM_PROPERTY")
    mapfile -t selenium_versions < <(fetch_versions "$SELENIUM_METADATA_URL")
    debug "fetched ${#selenium_versions[@]} selenium versions"
    SELENIUM_NEXT=$(pick_latest "$SELENIUM_CURRENT" major "${selenium_versions[@]}")
    if has_newer_out_of_scope "$SELENIUM_CURRENT" major "${selenium_versions[@]}"; then
        SELENIUM_OUT_OF_SCOPE=yes
    fi

    # Branch 8.3 has no spring.boot.version at all. That is its normal state,
    # not a problem, so it is reported and skipped rather than failing.
    if [[ $spring_count -eq 1 ]]; then
        SPRING_BOOT_CURRENT=$(prop_read "$pom" "$SPRING_BOOT_PROPERTY")
        mapfile -t spring_versions < <(fetch_versions "$SPRING_BOOT_METADATA_URL")
        debug "fetched ${#spring_versions[@]} spring boot versions"
        SPRING_BOOT_NEXT=$(pick_latest "$SPRING_BOOT_CURRENT" majorminor "${spring_versions[@]}")
        if has_newer_out_of_scope "$SPRING_BOOT_CURRENT" majorminor "${spring_versions[@]}"; then
            SPRING_BOOT_OUT_OF_SCOPE=yes
        fi
    else
        log "$branch: no <$SPRING_BOOT_PROPERTY> declared, skipping that property"
    fi

    rm -rf "$work"

    local -a moved=()
    if [[ -n $SELENIUM_NEXT ]]; then moved+=("selenium to $SELENIUM_NEXT"); fi
    if [[ -n $SPRING_BOOT_NEXT ]]; then moved+=("Spring Boot to $SPRING_BOOT_NEXT"); fi

    log "$REPO_SLUG [$branch]:"
    log "  $SELENIUM_PROPERTY: $SELENIUM_CURRENT${SELENIUM_NEXT:+ -> $SELENIUM_NEXT}"
    if [[ -n $SPRING_BOOT_CURRENT ]]; then
        log "  $SPRING_BOOT_PROPERTY: $SPRING_BOOT_CURRENT${SPRING_BOOT_NEXT:+ -> $SPRING_BOOT_NEXT}"
    fi

    if [[ ${#moved[@]} -eq 0 ]]; then
        RESULT_STATUS="up-to-date"
        log "  everything is up to date"
        return 0
    fi

    if [[ $dry_run == true ]]; then
        RESULT_STATUS="dry-run"
        log "  dry run, no branch or PR created"
        return 0
    fi

    require_gh_auth

    local title body existing bot_branch
    title=$(commit_title "$branch" "${moved[@]}")
    body=$(pr_body "$branch" "$reviewers")

    existing=$(find_existing_pr "$branch")
    if [[ -n $existing ]]; then
        bot_branch=$(printf '%s' "$existing" | jq -r .headRefName)
        RESULT_PR_NUMBER=$(printf '%s' "$existing" | jq -r .number)
        RESULT_PR_URL=$(printf '%s' "$existing" | jq -r .url)
        debug "reusing rolling PR #$RESULT_PR_NUMBER on $bot_branch"
        reset_branch_to_base "$bot_branch" "$branch"
    else
        bot_branch=$(find_unique_branch_name "$BOT_BRANCH_PREFIX-$branch-$(date -u +%Y-%m-%d)")
        debug "creating $bot_branch"
        git_r checkout --quiet -b "$bot_branch" "origin/$branch"
    fi

    git_r config user.name "github-actions[bot]"
    git_r config user.email "41898282+github-actions[bot]@users.noreply.github.com"

    if [[ -n $SELENIUM_NEXT ]]; then
        prop_replace "$REPO_DIR/$POM_PATH" "$SELENIUM_PROPERTY" "$SELENIUM_NEXT"
    fi
    if [[ -n $SPRING_BOOT_NEXT ]]; then
        prop_replace "$REPO_DIR/$POM_PATH" "$SPRING_BOOT_PROPERTY" "$SPRING_BOOT_NEXT"
    fi

    git_r add "$POM_PATH"
    git_r commit --quiet -m "$title" -m "$body"

    if [[ -n $RESULT_PR_NUMBER ]]; then
        git_r push --quiet --force-with-lease -u origin "$bot_branch"
        gh pr edit "$RESULT_PR_NUMBER" -R "$REPO_SLUG" --title "$title" --body "$body" >/dev/null
        request_reviewers "$RESULT_PR_NUMBER" "$reviewers"
        RESULT_STATUS="updated"
        log "  updated $RESULT_PR_URL"
    else
        git_r push --quiet -u origin "$bot_branch"

        local out=""
        out=$(gh pr create -R "$REPO_SLUG" --base "$branch" --head "$bot_branch" \
            --title "$title" --body "$body" 2>&1) ||
            die "gh pr create failed: ${out%%$'\n'*}"

        local url
        url=$(printf '%s\n' "$out" | grep -o 'https://[^[:space:]]*/pull/[0-9][0-9]*' | tail -1) ||
            die "could not parse a PR URL out of the gh output: ${out%%$'\n'*}"
        [[ -n $url ]] || die "could not parse a PR URL out of the gh output: ${out%%$'\n'*}"
        RESULT_PR_URL="$url"
        RESULT_PR_NUMBER="${url##*/}"
        apply_labels "$RESULT_PR_NUMBER"
        request_reviewers "$RESULT_PR_NUMBER" "$reviewers"
        RESULT_STATUS="created"
        log "  created $RESULT_PR_URL"
    fi

    return 0
}

trap on_exit EXIT
main "$@"
