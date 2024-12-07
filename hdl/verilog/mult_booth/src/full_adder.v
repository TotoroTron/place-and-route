module full_adder
(
    input wire i_a,
    input wire i_b,
    input wire i_cin,
    output wire o_sum,
    output wire o_cout
);
    wire sum_ab;
    wire cout_ab;

    wire sum_ab_cin;
    wire cout_ab_cin;

    half_adder ab
    (
        .i_a(i_a),
        .i_b(i_b),
        .o_sum(sum_ab),
        .o_cout(cout_ab)
    );

    half_adder ab_cin
    (
        .i_a(sum_ab),
        .i_b(i_cin),
        .o_sum(sum_ab_cin),
        .o_cout(cout_ab_cin)
    );

    assign o_sum = sum_ab_cin;
    assign o_cout = cout_ab | cout_ab_cin;

endmodule
