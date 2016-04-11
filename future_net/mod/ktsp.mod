# shortest path visiting a specified set of nodes

# weighted directed graph
param n integer, >0;
param s integer, >=0;
param t integer, >=0;
set V := 0..n-1;
set E within {V,V};
set P;
param c{E};

param M := (card(V)-1)*20;

# variables
var x{(u,v) in E} binary;
var w{u in V}, >=0;

# objective
minimize cost: 
  sum{(u,v) in E} c[u,v]*x[u,v];

# constraints
s.t. constraint1{v in V diff {s,t}} :
  sum{u in V : (v,u) in E} x[v,u] - sum{k in V : (k,v) in E}  x[k,v] = 0;

s.t. constraint2:
  sum{u in V : (u,s) in E} x[u,s] - sum{u in V : (s,u) in E} x[s,u] = -1;

s.t. constraint3:
  sum{u in V : (u,t) in E} x[u,t] - sum{u in V : (t,u) in E} x[t,u] = 1;

s.t. constraint4:
  sum{u in V diff {s} : (u,s) in E} x[u,s] = 0;

s.t. constraint5:
  sum{u in V diff {t} : (t,u) in E} x[t,u] = 0;
  
s.t. constraint6{v in P}:
  sum{u in V : (u,v) in E} x[u,v] = 1;
  
s.t. constraint7{(u,v) in E}:
  w[v] - w[u] <= c[u,v] + M*(1-x[u,v]);
  
s.t. constraint8{(u,v) in E}:
  w[v] - w[u] >= c[u,v] - M*(1-x[u,v]);

end;


