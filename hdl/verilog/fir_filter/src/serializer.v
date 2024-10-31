
module serializer 
#(
    parameter LENGTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_din_valid,
    input wire [LENGTH-1:0] iv_din,
    output wire o_dout
);

    reg [LENGTH-1:0] shift_reg;
    assign o_dout = shift_reg[0];

    always @(posedge i_clk) begin
        if (i_rst) begin
            shift_reg <= { (LENGTH){1'b0} };
        end else if (i_en) begin
            if (i_din_valid) begin
                shift_reg <= iv_din;
            end else begin
                shift_reg <= { 1'b0, iv_din[LENGTH-1:1] };
            end
        end
    end
endmodule
