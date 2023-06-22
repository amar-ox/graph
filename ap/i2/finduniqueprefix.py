infiles = ['atla', 'chic', 'hous', 'kans', 'losa', 'newy32aoa', 'salt', 'seat', 'wash']
v6prefixes = set()

for one_name in infiles:
    with open(one_name + 'v6', 'r') as in_f:
        for one_line in in_f:
            tokens = one_line.split()
            if len(tokens) > 2:
                v6prefixes.add((tokens[3], tokens[2]))
                
with open('v6prefixes', 'w') as out_f:
    for one_p in v6prefixes:
        print >> out_f, one_p[0], one_p[1]
