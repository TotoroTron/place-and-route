set x 1

if {$x == 2} {puts "$x is 2"} else {puts "$x is not 2"}

if {$x != 1} {
    puts "$x is != 1"
} else {
    puts "$x is 1"
}

if $x==1 {puts "GOT 1"}

#
# Be careful, this is just an example
# Usually you should avoid such constructs,
# it is less than clear what is going on and it can be dangerous
#
set y x
if "$$y != 1" {
    puts "$$y is != 1"
} else {
    puts "$$y is 1"
}

#
# A dangerous example: due to the extra round of substitution,
# the script stops
#
set y {[exit]}
if "$$y != 1" {
    puts "$$y is != 1"
} else {
    puts "$$y is 1"
}
