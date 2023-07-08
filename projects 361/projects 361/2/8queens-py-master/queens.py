# queens.py
# --------------------------------------------------------------
# 8 queens - solutions list
# --------------------------------------------------------------
# (2011.02.04)
# converted from q.pc (2008.05.25, 2010.02.19)
# [2017.03.05] - py2->py3

board = 8
x = [-1]*board


def print_result(a): print(''.join([str(b+1) for b in a]))

# *** Go! ***
y = 0

while True:
    if y == -1: break
    if y == board:
        print_result(x)
        y -= 1

    x[y] += 1

    if x[y] >= board:
        x[y] = -1
        y -= 1
        continue

    b = True
    for yy in range(y):
        if (
             (x[yy] == x[y]) or          # vert
             ((x[yy]-yy) == (x[y]-y)) or # \-d
             ((x[yy]+yy) == (x[y]+y))    # /-d
        ):
            b = False
            break
    if b: y += 1

# end
