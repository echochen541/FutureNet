# shortest path visiting a given set of nodes

# weighted directed graph
param n integer, >0;
param s integer, >=0;
param t integer, >=0;
set V := 0..n-1;
set A within V cross V;
set P;
param c{(u,v) in A};

param M := (card(V)-1)*20;

# variables
var y{(u,v) in A}, binary;
var w{u in V}, >=0;

minimize cost: 
  sum{(u,v) in A} c[u,v]*y[u,v];

s.t. consflow{v in V diff {s,t}} :
  sum{u in V : (v,u) in A} y[v,u] - sum{k in V : (k,v) in A}  y[k,v] = 0;

s.t. flows:
  sum{u in V : (u,s) in A} y[u,s] - sum{u in V : (s,u) in A} y[s,u] = -1;

s.t. flowt:
  sum{u in V : (u,t) in A} y[u,t] - sum{u in V : (t,u) in A} y[t,u] = 1;

#s.t. inflows:
#  sum{u in V diff {s} : (u,s) in A} y[u,s] = 0;

#s.t. outflowt:
#  sum{u in V diff {t} : (t,u) in A} y[t,u] = 0;
  
s.t. visit{v in P} :
  sum{u in V : (u,v) in A} y[u,v] = 1;
 
s.t. potentU{(u,v) in A}:
  w[v] - w[u] <= c[u,v] + M*(1-y[u,v]);
  
s.t. potentL{(u,v) in A}:
  w[v] - w[u] >= c[u,v] - M*(1-y[u,v]);

s.t. w_s: 
  	w[s]=0;

end;

