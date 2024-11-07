
module serializer 
#(
    parameter LENGTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [LENGTH-1:0] iv_din,
    input wire i_din_valid,
    input wire i_ready,
    output reg o_ready,
    output wire o_dout,
    output reg o_dout_valid
);

    reg [LENGTH-1:0] shift_reg;
    assign o_dout = shift_reg[0];

    localparam LENGTH_BITS = $clog2(LENGTH);
    reg [LENGTH_BITS-1:0] counter = { (LENGTH){1'b0} };

    always @(posedge i_clk) begin
        o_ready = 1'b0;
        o_dout_valid = 1'b0;
        if (i_rst) begin
            shift_reg = { (LENGTH){1'b0} };
        end else if (i_en) begin
            if (i_din_valid & i_ready) begin
                shift_reg = iv_din;
            end else begin
                shift_reg = { 1'b0, shift_reg[LENGTH-1:1] };
            end

            if (counter < LENGTH) begin
                counter = counter + 1;
            end else begin
                counter = 0;
                o_dout_valid = 1'b1;
                o_ready = 1'b1;
            end
        end
    end
endmodule
