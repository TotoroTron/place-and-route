module half_adder
(
    input wire i_a,
    input wire i_b,
    output wire o_sum,
    output wire o_cout
);

assign o_sum = i_a ^ i_b;
assign o_cout = i_a & i_b;

endmodule
