set x 0

if $x==0 {puts "clear 1"}

if "$x == 0" {puts "clear 2"}; # equivalent to: if "0 == 0" {...}

if {$x == 0} {puts "clear 3"}; # equivalent to: if $x==0 {...}
