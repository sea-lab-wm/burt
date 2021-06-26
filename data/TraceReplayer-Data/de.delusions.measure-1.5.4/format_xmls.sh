for f in *.xml; do 
	echo "Processing $f file.."; 
	xmllint --format $f > formatted_$f
done