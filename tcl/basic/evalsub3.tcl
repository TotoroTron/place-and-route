set x abc
puts "A simple substitution: $x\n"

# obtain the results of a command by placing the command in []
# all $variables are substituted by their values
set y [set x "def"]
puts "Remember that set returns the new value of the variable: X: $x Y: $y\n"

set z {[set x "This is a string within quotes within braces"]}
puts "Note the curly braces: $z\n"

set a "[set x {This is a string within braces within quotes}]"
puts "See how thte set is executed: $a"

puts "\$x is: $x\n"

set b "\[set y {This is a string within braces within quotes}]"
# Notes the \ escapes the bracket, and must be doubled to be a 
# literal char in double quotes
puts "Note the \\ escapes the bracket:\n \$b is: $b"
puts "\$y is : $y"
