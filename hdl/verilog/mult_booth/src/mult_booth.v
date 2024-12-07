
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

    // partial product wires
    wire [DATA_WIDTH-1:0] p[DATA_WIDTH-1:0];

    // intermediate carry and sum wires
    wire [(DATA_WIDTH*2)-2:0] c; // carry wires
    wire [(DATA_WIDTH*2)-2:0] s; // sum wires

    // partial products
    genvar i, j;
    generate
        for (i = 0; i < DATA_WIDTH; i = i + 1) begin
            for (j = 0; j < DATA_WIDTH; j = j + 1) begin
                assign p[i][j] = A[i] & B[j];
            end
        end
    endgenerate

    // assign the least significant bit of the output
    assign z[0] = p[0][0];

    // generate adders for each bit
    genvar row, col;
    generate
        // Loop through rows of adders
        for (row = 0; row < DATA_WIDTH - 1; row = row + 1) begin
            for (col = 0; col < DATA_WIDTH - row; col = col + 1) begin
                if (col == 0) begin
                    // half adder for the first column in each row
                    half_adder ha (
                        .i_a(       p[ row    ][ col+1 ] ),
                        .i_b(       p[ row+1  ][ col   ] ),
                        .o_sum(     z[ row+col+1 ]       ),
                        .o_cout(    c[ row+col   ]       )
                    );
                end else begin
                    // fulladder for the remaining columns
                    full_adder fa (
                        .i_a(       p[row][col + 1]),
                        .i_b(       c[row + col - 1]),
                        .i_cin(     s[row + col - 1]),
                        .o_sum(     s[row + col]),
                        .o_cout(    c[row + col])
                    );
                end
            end
        end
    endgenerate

    // handle final row
    generate
        for (col = 0; col < DATA_WIDTH - 1; col = col + 1) begin
            if (col == 0) begin
                half_adder ha_final (
                    .i_a(       c[ (DATA_WIDTH-2) + col  ]),
                    .i_b(       s[ (DATA_WIDTH-2) + col  ]),
                    .o_sum(     z[  DATA_WIDTH    + col  ]),
                    .o_cout(    c[ (DATA_WIDTH-1) + col  ])
                );
            end else begin
                full_adder fa_final (
                    .i_a(       c[ (DATA_WIDTH-2) + col  ] ),
                    .i_b(       s[ (DATA_WIDTH-2) + col  ] ),
                    .i_cin(     c[ (DATA_WIDTH-1) + col-1] ),
                    .o_sum(     z[  DATA_WIDTH    + col  ] ),
                    .o_cout(    c[ (DATA_WIDTH-1) + col  ] )
                );
            end
        end
        assign z[DATA_WIDTH*2-1] = c[(DATA_WIDTH*2)-2];
    endgenerate

endmodule
