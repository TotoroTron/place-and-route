puts "..... while loops"

set x 1

while {$x < 5} {
    puts "x is $x"
    set x [expr {$x + 1}]
}

puts "exited first loop with X equal to $x\n"


set x 0
while "$x < 5" {
    set x [expr {$x + 1}]
    if {$x > 7} break
    if "$x > 3" continue
    puts "x is $x"
}

puts "exited second loop with X equal to $x"


puts "\n..... for/incr loops"

for {set i 0}  {$i < 10} {incr i} {
    puts "I inside first loop: $i"
}

for {set i 3} {$i < 2} {incr i} {
    puts "I inside second loop: $i"
}


puts "Start"
set i 0
while {$i < 10} {
    puts "I inside third loop: $i"
    incr i
    puts "I after incr: $i"
}

set i 0
incr i
# this is equivalent to:
set i [expr {$i + 1}]


