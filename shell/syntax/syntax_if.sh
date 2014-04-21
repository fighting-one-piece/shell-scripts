score=40;
if [[ $score -gt 90 ]]; then
    echo "very good!";
elif [[ $score -gt 80 ]]; then
    echo "good!";
else
    echo "no pass!";
fi;
