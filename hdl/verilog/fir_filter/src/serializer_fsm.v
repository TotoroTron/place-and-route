

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

    parameter S0 = 4'b0001,
        S1 = 4'b0010,
        S2 = 4'b0100;

    reg [3:0] state = S0;
    reg [3:0] next_state;

    always @(posedge i_clk) begin
        state = 4'bxxxx;
        if (i_rst) state <= S0;
        else if (i_en) state <= next_state;
    end

    always @(state) begin
        next_state <= 4'bxxxx;
        case (state)
            S0: begin
                if (i_din_valid & i_ready) begin
                    o_ready <= 1'b0;
                    next_state <= S1;
                    shift_reg <= iv_din;
                end else begin
                    o_ready <= 1'b1;
                    next_state <= S0;
                end
            end

            S1: begin
                next_state <= S1;
                shift_reg <= { 1'b0, shift_reg[LENGTH-1:1] };
                if (counter < LENGTH) begin
                    counter <= counter + 1;
                end else begin
                    counter <= 0;
                    o_dout_valid <= 1'b1;
                    o_ready <= 1'b1;
                    next_state <= S2;
                end
            end

            S2: begin
                if (i_ready) begin
                    next_state <= S0;
                end else begin
                    next_state <= S2;
                end
            end

            default: begin
                next_state <= 4'bxxxx;
            end
        endcase
    end



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
