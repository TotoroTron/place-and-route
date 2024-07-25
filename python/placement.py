from abc import ABC

class GlobalPlacement(ABC):
    def __init__(self, netlist):
        self.hypergraph = netlist
        self.graph = []

        ...
        pass

    def bound2bound(self, net):
        pass

    def to_graph(self, hypergraph):
        # hypergraph = (hyperedges, vertices)
        hyperedges, vertices = hypergraph
        # hyperedges = ()
        # SimPL and Kraftwerks2 uses bound2bound, not star-model


        for he in hyperedges:
            pass

            
        ...
        pass
    
    @abstractmethod
    def solve(self, graph)
        ...
        pass

    ...


class SimulatedAnnealing(GlobalPlacement):
    ...


class AnalyticalPlacment(GlobalPlacement):
    ...


class ElectrostaticPlacement(GlobalPlacement):
    ...

