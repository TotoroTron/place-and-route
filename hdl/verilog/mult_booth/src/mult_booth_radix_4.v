
//
// NEED TO GENERALIZE FOR ANY DATA_WIDTH
//

module mult_booth_radix_4
#(
    parameter DATA_WIDTH = 16
)(
    input [DATA_WIDTH-1:0] iv_a, // multiplier
    input [DATA_WIDTH-1:0] iv_b, // multiplicand
    output [DATA_WIDTH*2-1:0] ov_prod
);

    //   multiplicand        aaaa  
    //     multiplier x      bbbb x
    // ----------------    --------
    //
    // radix 4 booth's encoding
    //
    // for i in range {1, 2, ..., NUM_PARTIALS}
    //
    // x(2i+1) x(2i) x(2i-1) | i-th partial product
    //                  000  |   0 << 2i
    //                  001  |  +A << 2i
    //                  010  |  +A << 2i
    //                  011  | +2A << 2i
    //                  100  | -2A << 2i
    //                  101  |  -A << 2i
    //                  110  |  -A << 2i
    //                  111  |   0 << 2i

    // booth encoding groups
    localparam NUM_PARTIALS = (DATA_WIDTH + 1) / 2; // ALSO PARTIAL STACK SIZE

    // decision vectors
    wire [NUM_PARTIALS-1:0] sel[2:0]; // WHICH OPERATION?

    // partial products
    wire [DATA_WIDTH-1:0] partials[DATA_WIDTH-1:0];

    // accumulator
    localparam ACC_WIDTH = $clog2(NUM_PARTIALS) + DATA_WIDTH;
    reg [ACC_WIDTH-1:0] accumulator;

    localparam SIGN_EXTEND = (DATA_WIDTH % 2 == 0) ? 2 : 0;





endmodule
