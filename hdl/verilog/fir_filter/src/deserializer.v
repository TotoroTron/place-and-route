
module deserializer 
#(
    parameter LENGTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_din,
    input wire i_din_valid,
    output reg [LENGTH-1:0] ov_dout,
    output reg o_dout_valid
);

    reg [LENGTH-1:0] shift_reg;

    always @(posedge i_clk) begin
        ov_dout = ov_dout;
        o_dout_valid = o_dout_valid;
        if (i_rst) begin
            o_dout_valid = 1'b0;
            shift_reg = { (LENGTH){1'b0} };
            ov_dout = { (LENGTH){1'b0} };
        end else if (i_en) begin
            o_dout_valid = 1'b0;
            shift_reg = { i_din, shift_reg[LENGTH-1:1] };
            if (i_din_valid) begin 
                ov_dout = shift_reg;
                o_dout_valid = 1'b1;
            end
        end
    end
endmodule
