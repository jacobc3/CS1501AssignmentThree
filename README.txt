Author@Shuwen Zhou 
(SHZ58)

$ time cat lecturegraph.txt | java CreateAuxiliaryGraph | java ApplyBellmanFord 6 | java ApplyDijkstra 0
(6-7)
real	0m0.196s
user	0m0.233s
sys	0m0.025s



$ time cat lecturegraph.txt | java CreateAuxiliaryGraph | java ApplyBellmanFord 6 | java ApplyDijkstraAllPairs
(6-7)
real	0m0.161s
user	0m0.218s
sys	0m0.041s



$ time cat tinyEWDn.txt | java CreateAuxiliaryGraph | java ApplyBellmanFord 8 | java ApplyDijkstra 0
(8-15)
real	0m0.157s
user	0m0.229s
sys	0m0.041s


$ time cat tinyEWDn.txt | java CreateAuxiliaryGraph | java ApplyBellmanFord 8 | java ApplyDijkstraAllPairs
(8-15)
real	0m0.186s
user	0m0.232s
sys	0m0.061s


$ time cat 10-21 | java CreateAuxiliaryGraph | java ApplyBellmanFord  10 | java ApplyDijkstra 0
(10-21)
real	0m0.176s
user	0m0.202s
sys	0m0.089s

$ time cat 10-21 | java CreateAuxiliaryGraph | java ApplyBellmanFord  10 | java ApplyDijkstraAllPairs
(10-21)
real	0m0.224s
user	0m0.280s
sys	0m0.062s


$ time cat 12-30 | java CreateAuxiliaryGraph | java ApplyBellmanFord  12 | java ApplyDijkstra 0
(12-30)
real	0m0.177s
user	0m0.258s
sys	0m0.026s



$ time cat 12-30 | java CreateAuxiliaryGraph | java ApplyBellmanFord  12 | java ApplyDijkstraAllPairs
(12-30)
real	0m0.240s
user	0m0.292s
sys	0m0.057s
