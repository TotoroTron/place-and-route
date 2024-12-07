
//
// NEED TO GENERALIZE FOR ANY DATA_WIDTH
//

module mult_booth
#(
    parameter DATA_WIDTH = 16
)(
    input [DATA_WIDTH-1:0] A,
    input [DATA_WIDTH-1:0] B,
    output [DATA_WIDTH*2-1:0] z
);
    wire p[3:0][3:0];
    wire [10:0] c; // c represents carry of HA/FA
    wire [5:0] s;  // s represents sum of HA/FA

    genvar g;
    generate
        for(g = 0; g<4; g=g+1) begin
            assign p[g][0] = A[g] & B[0];
            assign p[g][1] = A[g] & B[1];
            assign p[g][2] = A[g] & B[2];
            assign p[g][3] = A[g] & B[3];
        end
    endgenerate

    assign z[0] = p[0][0];

    //row 0
    half_adder h0(p[0][1], p[1][0], z[1], c[0]);
    half_adder h1(p[1][1], p[2][0], s[0], c[1]);
    half_adder h2(p[2][1], p[3][0], s[1], c[2]);

    //row1
    full_adder f0(p[0][2], c[0], s[0], z[2], c[3]);
    full_adder f1(p[1][2], c[1], s[1], s[2], c[4]);
    full_adder f2(p[2][2], c[2], p[3][1], s[3], c[5]);

    //row2
    full_adder f3(p[0][3], c[3], s[2], z[3], c[6]);
    full_adder f4(p[1][3], c[4], s[3], s[4], c[7]);
    full_adder f5(p[2][3], c[5], p[3][2], s[5], c[8]);

    //row3
    half_adder h3(c[6], s[4], z[4], c[9]);
    full_adder f6(c[9], c[7], s[5], z[5], c[10]);
    full_adder f7(c[10], c[8], p[3][3], z[6], z[7]);

endmodule
