set Z Albany
set Z_LABEL "The Capitol of New York is: "

puts "$Z_LABEL $Z"; # prints the value of Z
puts "$Z_LABEL \$Z"; # prints a literal $Z instead of value of Z
# backslash generally disables substitution for the single char immediately after \
# but there are some special backslash sequences get substituted by specific values
# \n New Line
# \r Carriage Return
# \v Vertical Tab
# \b Backspace
# etc.

puts "\nBen Franklin is on the \$100.00 bill"

set a 100.00
puts "Washington is not on the $a bill";    # this is not what you want
puts "Lincoln is not on the $$a bill";      # this is OK
puts "Hamilton is not on the \$a bill";     # this is not what you want
puts "Ben Franklin is on the \$$a bill";    # this is OK but redundant


puts "\n.................. examples of escape strings"
puts "Tab/tTab/tTab"
puts "This string prints out \non two lines"
puts "This string comes out\
      on a single line."
