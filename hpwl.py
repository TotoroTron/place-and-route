from abc import ABC

class HPWL(ABC):
    ...
    pass

class Manhattan(HPWL):
    """
    Manhattan Distance Half Perimeter Wire Length.
    - Convex, but NOT strictly convex => will often yield many solutions.
    - Non-differentiable due to piecewise functions.

    """
    ...
    pass

class Euclidean(HPWL):
    """
    Euclidean Distance Half Perimeter Wire Length.
    - Strictly Convex => Yields unique solution.
    - Differentiable, Quadratic => Can use 1st and 2nd order methods.

    HPWL(x, y) = HPWLx(x) + HPWLy(y)

    HPWLx(x) = sum_i,j : w_i,j (x_i - x_j)^2
    HPWLy(y) = sum_i,j : w_i,j (y_i - y_j)^2

    HPWL recast into matrix form:
    HPWLx(x) = (1/2).x^T.Q_x.x + c_x^T.x + const
    HPWLy(y) = (1/2).y^T.Q_y.y + c_y^T.y + const
    
    """
    ...
    pass

