# Last Edited by Richard Bonett: 3/25/2017
#
# This script runs the AAPTParser on a directory
# of directories, where each subdirectory contains
# a number of apk files
#
# Usage:
#  aapt.sh PATH_TO_AAPTPARSER PATH_TO_AAPT INPUT_ROOT_DIRECTORY OUTPUT_FILE

echo "[" > "$4"
for dir in $3/*; do 
  java -jar $1 $2 "$dir" ~/aapt_tmp.json
  head --lines=-1 ~/aapt_tmp.json | tail --lines=+2 >> "$4"
  echo "," >> "$4"
done
rm ~/aapt_tmp.json
echo "{}]" >> "$4"
