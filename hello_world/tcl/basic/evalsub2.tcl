# grouping args with "" allows substitutions to allow within the ""
# in constrast, grouping args with {} disables substituions within the {}

set Z Albany
set Z_LABEL "The Capitol of NY is: "

puts "\n...... examples of differences between \" and \}"
puts "$Z_LABEL $Z"
puts {$Z_LABEL $Z}

puts "\n...... examples of differences in nesting \{ and \" "
puts "$Z_LABEL {$Z}"
puts {Who said, "What this country needs is a good $0.05 cigar! "?}

puts "\n...... examples of escape strings"
puts {There are no substitutions done within braces \n \r \x0a \f \v}
puts {But, the escaped newline at the end of a\
    string is still evaluated as a space.}

