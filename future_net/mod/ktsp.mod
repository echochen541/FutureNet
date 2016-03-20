# shortest path problem (Rafael - potential)

# graph directed
param n integer, >0;
param orig integer, >=0;
param dest integer, >=0;
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

subject to consflow{v in V diff {orig,dest}} :
  sum{u in V : (v,u) in A} y[v,u] - sum{k in V : (k,v) in A}  y[k,v] = 0;

subject to floworig:
  sum{u in V : (u,orig) in A} y[u,orig] - sum{u in V : (orig, u) in A} y[orig,u] = -1;

subject to flowdest:
  sum{u in V : (u,dest) in A} y[u,dest] - sum{u in V : (dest,u) in A} y[dest,u] = 1;

#subject to infloworig:
#  sum{u in V diff {orig} : (u,orig) in A} y[u,orig] = 0;

#subject to outflowdest:
#  sum{u in V diff {dest} : (dest,u) in A} y[dest,u] = 0;
  
subject to visit{v in P  diff {orig,dest}} :
  sum{u in V : (u,v) in A} y[u,v] = 1;
 
subject to potentU{(u,v) in A}:
  w[v] - w[u] <= c[u,v] + M*(1-y[u,v]);
  
subject to potentL{(u,v) in A}:
  w[v] - w[u] >= c[u,v] - M*(1-y[u,v]);

subject to w_orig: 
  	w[orig]=0;

end;

