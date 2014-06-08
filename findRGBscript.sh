#!/bin/bash
#nested loop


#loops through all the possible RGB values at increments of 10 
#sends HTTP POST to easyrgb.com with RGB value parameters and color collection parameters
#parses HTML page result to obtain word description using grep
#writes data to text file

for ((i=0; i<=255; i=i+10))
do
	for ((j=0; j<=255; j=j+10))
	do
		for ((k=0;k<=255; k=k+10))
		do
			RESULT=`curl --data "E1=$i&E2=$j&E3=$k&LX=ColorChecker+(Munsell-GretagMacbeth-Xrite)&Match.x=58&Match.y=10" http://www.easyrgb.com/index.php?X=SEEK | grep -o -P '(?<=Match #1).*(?=<BR>Match #2)' | grep -Po '".*?"'`
			echo "$i,$j,$k,$RESULT\n" >> rgbvalues2.txt
			done  
	done
done
