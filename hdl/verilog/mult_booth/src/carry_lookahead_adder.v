
module carry_lookahead_adder
#(
    parameter DATA_WIDTH = 16
)(
    input wire [DATA_WIDTH-1:0] iv_a,
    input wire [DATA_WIDTH-1:0] iv_b,
    input wire i_cin,
    output wire [DATA_WIDTH-1:0] ov_sum,
    output wire o_cout
);
    //
    // c0 = c0
    // c1 = g0 | p0.c0
    // c2 = g1 | p1.g0 | p1.p0.c0
    // c3 = g2 | p2.g1 | p2.p1.g0 | p2.p1.p0.c0
    // c4 = g3 | p3.g2 | p3.p2.g1 | p3.p2.p1.g0 | p3.p2.p1.p0.c0
    // c5 = g4 | p4.g3 | p4.p3.g2 | p4.p3.p2.g1 | p4.p3.p2.p1.g1 | p5.p4.p3.p2.p1.p0.c0
    // etc..
    //

    wire [DATA_WIDTH-1:0] p, g;
    wire [DATA_WIDTH:0] c;

    assign c[0] = i_cin;

    genvar i;

    // compute generate and propagation
    generate for (i = 0; i < DATA_WIDTH; i = i + 1) begin
        assign p[i] = iv_a[i] ^ iv_b[i];
        assign g[i] = iv_a[i] & iv_b[i];
    end endgenerate

    // compute carry for each stage
    generate for (i = 1; i < DATA_WIDTH+1; i = i + 1) begin
        assign c[i] = g[i-1] | ( p[i-1] & c[i-1] );
    end endgenerate

    // compute sum
    generate for (i = 0; i < DATA_WIDTH; i = i + 1) begin
        assign ov_sum[i] = p[i] ^ c[i];
    end endgenerate

    // assign final carry
    assign o_cout = c[DATA_WIDTH];

endmodule
