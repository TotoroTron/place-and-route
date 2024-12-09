
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

    // partial product wires

    // booth encoding groups
    localparam NUM_PARTIALS = DATA_WIDTH/2 + 1; // ALSO PARTIAL STACK SIZE

    // decision vectors
    wire [NUM_GROUPS-1:0] decisions[2:0]; // WHICH OPERATION?

    // partial sums
    wire [DATA_WIDTH-1:0] partials[DATA_WIDTH-1:0];

    // accumulator
    localparam ACC_WIDTH = $clog2(NUM_PARTIALS) + DATA_WIDTH;
    reg [ACC_WIDTH-1:0] accumulator;

    localparam SIGN_EXTEND = (DATA_WIDTH % 2 == 0) ? 2 : 0;





endmodule
