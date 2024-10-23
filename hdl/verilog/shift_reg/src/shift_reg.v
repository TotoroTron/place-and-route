module shift_reg
#(parameter LENGTH = 8)
    (
        input i_clk,
        input i_rst,
        input i_din,
        output o_dout
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
