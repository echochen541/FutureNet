# shortest path visiting a given set of nodes
# weighted directed graph
param n integer, >0;
param s integer, >=0;
param t integer, >=0;
set V := 0..n-1;
set A within {V,V};
set P;
param c{A};

param M := (card(V)-1)*20;

# variables
var y{(u,v) in A} binary; #<= 1, >= 0; # arc flow
var w{u in V} >=0; # node potential

minimize cost: 
  sum{(u,v) in A} c[u,v]*y[u,v];

subject to consflow{v in V diff {s,t}} :
  sum{u in V : (v,u) in A} y[v,u] - sum{k in V : (k,v) in A}  y[k,v] = 0;

subject to flows:
  sum{u in V : (u,s) in A} y[u,s] - sum{u in V : (s,u) in A} y[s,u] = -1;

subject to flowt:
  sum{u in V : (u,t) in A} y[u,t] - sum{u in V : (t,u) in A} y[t,u] = 1;

subject to inflows:
  sum{u in V diff {s} : (u,s) in A} y[u,s] = 0;

subject to outflowt:
  sum{u in V diff {t} : (t,u) in A} y[t,u] = 0;

subject to visit{v in P  diff {s,t}} :
  sum{u in V : (u,v) in A} y[u,v] = 1;

subject to potentU{(u,v) in A}:
  w[v] - w[u] <= c[u,v] + M*(1-y[u,v]);

subject to potentL{(u,v) in A}:
  w[v] - w[u] >= c[u,v] - M*(1-y[u,v]);

subject to w_s: 
  	w[s]=0;

end;

