
module deserializer 
#(
    parameter LENGTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_din_valid,
    input wire i_din,
    output reg [LENGTH-1:0] ov_dout
);

    reg [LENGTH-1:0] shift_reg;

    always @(posedge i_clk) begin
        ov_dout <= ov_dout;
        if (i_rst) begin
            shift_reg <= { (LENGTH-1){1'b0} };
        end else if (i_en) begin
            shift_reg <= { i_din, shift_reg[LENGTH-1:1] };
            if (i_din_valid) begin 
                ov_dout <= shift_reg;
            end
        end
    end
endmodule
