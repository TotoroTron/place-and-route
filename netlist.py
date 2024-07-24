from abc import ABC

class Netlist(ABC):
    """

    Netlist N = (Eh, V) # (hyperedges, vertices)
        Eh = [e1, e2, ... ]
            e = (source, sinks)
                sinks = [sink1, sink2, ... ]
        V = [v1, v2, ... ]
            v = (x, y) # x-y location on the chip

    Convert netlist from hypergraph to graph using star-model for all hyperedges.
    For every net, make a 2-pin edge between sink vertex to the source vertex for every sink.
    Remove original hyperedge.
    Graph G = (Eg, V) # edges instead of hyperedges. 

    """

    ...
    pass
