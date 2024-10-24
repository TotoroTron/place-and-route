module shift_reg
#(parameter LENGTH = 8)
    (
        input wire i_clk,
        input wire i_rst,
        input wire i_din,
        output wire o_dout
    );

    reg [LENGTH-1:0] shift_reg;
    assign o_dout = shift_reg[LENGTH-1];

    always @(posedge i_clk) begin
        if (i_rst) begin
            shift_reg = { (LENGTH-1){1'b0} };
        end else begin
            shift_reg = { shift_reg[LENGTH-2:0], i_din };
        end
    end

endmodule
