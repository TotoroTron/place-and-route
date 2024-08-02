library ieee;
use ieee.std_logic_1164.all;
use std.env.finish;

entity testbench is
end testbench;

architecture behavioral of testbench is
    signal tb_clk : std_logic := '0';
    signal tb_rst : std_logic := '1';
    signal tb_led : std_logic_vector(3 downto 0) := (others => '0');
begin
    
    
    UUT : entity work.top_level
    -- generic map(tps => 1000000) -- ticks per second
    port map(
        i_clk => tb_clk,
        i_rst => tb_rst,
        ov_led => tb_led
    );

    tb_clk <= not tb_clk after 5 ns;

    STIMULI : process
    begin
        tb_rst <= '1';
        wait for 25 ns;
        tb_rst <= '0';

        wait for 100 us;
        tb_rst <= '1';
        wait for 100 us;
        tb_rst <= '0';
        wait for 100 us;
        finish;


    end process;

end behavioral;
