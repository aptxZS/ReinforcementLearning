#!/bin/bash
r1=0
r2=0
r3=0
N=50
rounds=100
unit=1
seeds_no=8
echo $1
for i in $(seq 1 $N)
do
    printf "$i "
    seed=$(echo "$i % $seeds_no" | bc)
    out=$(java -cp bin main.RunLemonadeGame -seed $seed -rounds $rounds -repeats 1 $1 "" $2 "" $3 "" | tail -n1 | tail -c +2 | head -c -2)
    v1=$(echo $out | awk '{print $1;}')
    r1=$(echo "$v1 * $unit + $r1" | bc)
    v2=$(echo $out | awk '{print $2;}')
    r2=$(echo "$v2 * $unit + $r2" | bc)
    v3=$(echo $out | awk '{print $3;}')
    r3=$(echo "$v3 * $unit + $r3" | bc)
done
echo
r1=$(echo "$r1 / $N / $rounds" | bc -l)
r2=$(echo "$r2 / $N / $rounds" | bc -l)
r3=$(echo "$r3 / $N / $rounds" | bc -l)
echo "$1 $2 $3"
echo "$r1 $r2 $r3"
