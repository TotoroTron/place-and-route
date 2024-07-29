library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity counter is
    generic (width : positive := 4); -- counter width
    port (
        i_clk : in std_logic;
        i_rst : in std_logic;
        i_en : in std_logic;
        ov_count : out std_logic_vector(width-1 downto 0)
    );
end counter;

architecture behavioral of counter is
    signal count : std_logic_vector(width-1 downto 0) := (others => '0');
begin

    ov_count <= count;

    process(i_clk, i_rst, i_en)
    begin
        if rising_edge(i_clk) then
            if i_rst = '1' then
                count <= (others => '0');
            elsif i_en = '1' then
                count <= std_logic_vector(unsigned(count) + 1); 
            end if;
        end if;
    end process;


end behavioral;
