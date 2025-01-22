

module deserializer_fsm
#(
    parameter LENGTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_din,
    input wire i_din_valid,
    input wire i_ready, // from fir: fir ready or not
    output reg o_ready, // to testbench: deserializer ready or not
    output reg [LENGTH-1:0] ov_dout,
    output reg o_dout_valid
);

    reg [LENGTH-1:0] shift_reg;
    localparam LENGTH_BITS = $clog2(LENGTH);
    reg [LENGTH_BITS-1:0] counter;

    parameter 
        IDLE = 3'b001,
        SHIFT_IN = 3'b010,
        OUTPUT = 3'b100;
    reg [2:0] state = IDLE;
    reg [2:0] next_state;

    // STATE REGISTER
    always @(posedge i_clk) begin
        if (i_rst) state <= IDLE;
        else if (i_en) state <= next_state;
    end

    // STATE MACHINE
    always @(*) begin
        case (state)
            IDLE: begin
                next_state <= IDLE;
                if (i_din_valid)
                    next_state <= SHIFT_IN;
            end

            SHIFT_IN: begin
                next_state <= SHIFT_IN;
                if (counter == LENGTH - 1)
                    next_state <= OUTPUT;
            end

            OUTPUT: begin
                next_state <= OUTPUT;
                if (i_ready)
                    next_state <= IDLE;
            end

            default: begin
                next_state <= IDLE;
            end
        endcase
    end

    // OUTPUT LOGIC
    always @(posedge i_clk) begin
        if (i_rst) begin
            o_ready      <= 1'b0;
            o_dout_valid <= 1'b0;
            counter      <= 0;
            shift_reg    <= 0;
        end else if (i_en) begin
            o_ready      <= 1'b0;
            o_dout_valid <= 1'b0;

            case (state)
                IDLE: begin
                    counter         <= 0;
                    shift_reg       <= 0;
                end

                SHIFT_IN: begin
                    o_ready         <= 1'b1;
                    // if (i_din_valid && o_ready) begin
                    if (i_din_valid) begin
                        shift_reg   <= { i_din, shift_reg[LENGTH-1:1] };
                        counter     <= counter + 1;
                    end
                end

                OUTPUT: begin
                    o_dout_valid    <= 1'b1;
                    ov_dout         <= shift_reg;
                end

                default: begin
                end
            endcase
        end
    end
endmodule
