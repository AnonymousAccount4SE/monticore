#!/bin/bash
# script for all preprocessing steps of the pages job
# is used to have uniform bases for both gitlab and github pages
# is used from '.gitlab-ci.yml'(gitlab) and '.travis.yml'(github)

# execute report scripts and print output to *.md file, to use these in pages
mkdir docs/_intern
sh docs/scripts/errorList.sh '../../' 'target/site/errorList' > docs/_intern/ErrorList.md
sh docs/scripts/detailedErrorList.sh '../../' 'target/site/detailedErrorList' > docs/_intern/DetailedErrorList.md
sh docs/scripts/findDoubleFileNames.sh './' 'target/site/findDoubleFileNames' > docs/_intern/FindDoubleFileNames.md
sh docs/scripts/ftlAnalysis.sh './' 'configure.StartAllOutput' 'target/site/ftlAnalysis' > docs/_intern/FtlAnalysis.md
echo "[INFO] Executed report scripts for pages"

# move all directories that contain *.md files to the docs folder
# because mkdocs can only find *.md files there
mv monticore-grammar docs/monticore-grammar
mv monticore-runtime docs/monticore-runtime
mv 00.org docs/00.org
mv *.md docs/
mv *.png docs/
echo "[INFO] Moved *.md files to 'docs' folder"