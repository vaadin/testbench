# `.github/scripts`

## `bump-dependencies.sh`

Keeps the `selenium.version` and `spring.boot.version` properties in the root
`pom.xml` current across the maintained branches. Driven nightly by
[`.github/workflows/bump-dependencies.yml`](../workflows/bump-dependencies.yml),
which opens one rolling pull request per branch.

### The two version policies

They differ because the two projects release differently.

| property | scope | example |
|---|---|---|
| `selenium.version` | newest stable release on the same **major** | `4.44.0` → `4.48.0` |
| `spring.boot.version` | newest stable release on the same **major.minor** | `4.0.4` → `4.0.8` |

Selenium ships a new *minor* for every release and treats them as ordinary
updates, so crossing a minor is in scope: a lagging branch catches up in a
single PR rather than one minor per night. Spring Boot minors change what a
maintenance branch ships, so those stay a human decision.

One deliberate exception: a branch pinned to a prerelease of its own line
(`main` and `25.2` sit on `4.1.0-RC1`) is allowed to move up to a *stable*
release on that same line, `4.1.0-RC1` → `4.1.1`. That unpins a milestone once
GA ships without changing which line the branch is on.

Neither property ever crosses a major.

Both anchor on the branch's own current value, never on the metadata's
`<latest>`/`<release>` fields. Those cannot be trusted here:
`spring-boot-starter-parent` reports a milestone of a newer minor
(`4.2.0-M1` at the time of writing), which would drag every maintenance branch
onto a prerelease of the wrong line.

A branch with no `spring.boot.version` at all — `8.3` — is reported and skipped
for that property, not treated as an error.

### Rolling pull requests

Each run reuses this job's own open PR for the branch and resets its branch
back to base, so the PR stays a **single** commit instead of accumulating one
bump commit per night. The PR is identified by its head branch prefix
(`bot/bump-dependencies-*`), not by its title, so it never adopts a human's PR
and force-pushes over it.

Because the bot branch is force-pushed, do not push fixups to it. Merge the PR
and let the next run open a fresh one, or bump the property by hand.

### Running it locally

```bash
# The assertions -- no network, no git, no gh.
bash .github/scripts/bump-dependencies.sh --self-test

# What would a run do to a branch? Read-only.
bash .github/scripts/bump-dependencies.sh --branch 9.5 --dry-run
```

A real run needs an authenticated `gh` and a checkout it may switch branches
in. It refuses to operate on its own checkout, because checking out a release
branch would delete the script mid-run — it does not exist on `8.3`. Give it a
separate clone or worktree:

```bash
git worktree add ../testbench-bump
bash .github/scripts/bump-dependencies.sh --branch 9.5 --repo-dir ../testbench-bump
```

Requires `bash` 4+, `curl`, `git`, `jq`, and `gh` for anything but `--dry-run`.
`--help` lists every option.

### Changing which branches are covered

Edit `DEFAULT_BRANCHES` (and the `branches` input default) in
[`bump-dependencies.yml`](../workflows/bump-dependencies.yml). Nothing in the
script knows the branch list.

### The main-branch caveat

GitHub Actions only honors `schedule` on the default branch, and a
`workflow_dispatch` always reads the workflow file from `main`. Both the
workflow and this script must therefore be on `main` before anything can run.
The release branches need no copy of either — one code path, edited in one
place, which is why the workflow uses two checkouts (the script from `main`,
the target branch in `target/`).

### Tokens

The workflow prefers `secrets.GHTK` and falls back to `GITHUB_TOKEN`.

The fallback opens working pull requests, but they are authored by
`github-actions[bot]`, and this repo requires approval for workflow runs from
contributors it treats as external. `validation.yml` is therefore created and
immediately parked with conclusion `action_required` until a maintainer
approves it on the PR's Checks tab -- observed on #2298, whose first attempt
concluded `action_required` and only ran once approved by hand. Closing and
reopening the PR does not help, because the reopen is attributed to the bot as
well.

Making the PAT available as `secrets.GHTK` (sharing the org secret with this
repo) fixes it permanently and needs no change here: the PR is then authored by
a collaborator, so nothing holds the run. The job summary says so on any run
that had to fall back.
