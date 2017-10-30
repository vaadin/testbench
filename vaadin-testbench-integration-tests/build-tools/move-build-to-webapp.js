#!/usr/bin/env node

// Move files from the "build" directory to the target dir

const rimraf = require('rimraf');
const mkdirp = require('mkdirp');
const fs = require('fs');

const frontendVaadin = "../../../#frontend-target-folder#";

if (!fs.existsSync(frontendVaadin)) {
	mkdirp.sync(frontendVaadin);
}

for (dir of ["frontend-es5","frontend-es6"]) {
    const sourceDir = "build/" + dir;
    const targetDir = "../"+dir;

    rimraf.sync(targetDir);
    fs.renameSync(sourceDir, targetDir);
}
