library ieee;
use ieee.std_logic_1164.all;

entity pps_gen is
    generic(pulse_freq : positive := 1); -- pulses per second
    port(
        i_clk : in std_logic;
        i_rst : in std_logic;
        o_pulse : out std_logic
    );
end pps_gen;

architecture behavioral of pps_gen is
    constant clk_freq : integer := 125_000_000;
    constant clks_per_pulse : integer := clk_freq / pulse_freq;
    signal count : integer := 0; 
    signal count_next : integer := 0; 
begin

    process(i_clk, i_rst)
    begin
    if rising_edge(i_clk) then
        if i_rst = '1' then
            count <= 0; 
            o_pulse <= '0';
        else
            if (count = clks_per_pulse - 1) then
                count <= 0;
                o_pulse <= '1';
            else
                count <= count + 1;
                o_pulse <= '0';
            end if;
        end if;
    end if; -- end rising_edge
    end process;


    -- https://hardwaredescriptions.com/elementor-fixed-point-arithmetic-in-synthesizable-vhdl/
end behavioral;
