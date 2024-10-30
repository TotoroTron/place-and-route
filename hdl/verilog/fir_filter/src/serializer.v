
module serializer 
#(
    parameter LENGTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_latch,
    input wire i_din_valid,
    input wire [LENGTH-1:0] iv_din,
    output reg o_dout
);

    reg [LENGTH-1:0] shift_reg;

    always @(posedge i_clk) begin
        
        if (i_rst) begin
            shift_reg <= { (LENGTH){1'b0} };
            o_dout <= 1'b0;

        end else if (i_en) begin
            if (!i_din_valid) begin
                shift_reg <= iv_din;
            end else if (i_din_valid) begin
                shift_reg <= { 1'b0, iv_din[LENGTH-1:1] };
                o_dout <= shift_reg[0];
            end
        end
    end
endmodule
