set fruit Cauliflower; # assignment op.
# places "Cauliflower" in the memory space referenced by "fruit".

puts fruit; # prints "fruit" 
puts $fruit; # prints "Cauliflower"


# set varName ?value?
#     If value is specified, then the contents of the variable varName are set equal to value . 
#     If varName consists only of alphanumeric characters, and no parentheses, it is a scalar variable. 
#     If varName has the form varName(index) , it is a member of an associative array. 

#  Tcl passes data to subroutines either by name or by value. 
#  Commands that don't change the contents of a variable usually have their arguments passed by value.
#  Commands that do change the value of the data must have the data passed by name. 

set X "This is a string"

set Y 1.24

puts $X
puts $Y

puts "..............................."

set label "The value in Y is: "
puts "$label $Y"
