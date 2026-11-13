Selenium's own reads, run in the page so that the Playwright backend reads what Selenium reads.
All Apache-2.0 (`LICENSE`), copied unchanged.

- `getAttribute.js`, `isDisplayed.js`: from selenium-remote-driver 4.52.0
  (`org/openqa/selenium/remote/`), the atoms Selenium's Java client runs through `executeScript`
  for `getAttribute` and `isDisplayed`.
- `get-text.js`: chromedriver's compiled `GET_TEXT`, the C string literals of Chromium's
  `third_party/selenium-atoms/atoms.cc` (main, 2026-11-11) joined and unescaped. Built from
  Selenium c0b8ff64 plus Chromium's `patch.diff`, which treats every shadow root as shown, so it
  returns what Selenium's `getText()` returns on Chrome.
