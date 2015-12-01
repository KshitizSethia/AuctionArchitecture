# AuctionArchitecture
Architecture for Auction game in Heuristic Problem Solving (Fall 2015)
Instructions for game at: http://cs.nyu.edu/courses/fall15/CSCI-GA.2965-001/auction.html

##Communication

###Variables:
**item index**: Index of item (integer)

**item type**: type of item ex: t1, t2, t3

**bid value**: value of bid (float)


####Step 1
Client will connect at specified port. If server is ready, communication will start. If server doesn't connect, then client should keep retrying.
####Step 2
On connecting, the server will send the current state to client. At game end, the server will send "FINISHED", in all other cases:

Format: List of (&lt;item index&gt;, &lt;item type&gt;, &lt;winner of item ('-' if item not won yet)&gt;, &lt;value at which item was won (0 if not won yet)&gt;)

Example:

(0,t5,-,0.000000)

(1,t4,-,0.000000)


####Step 3
Client can now place a move.

Format: &lt;authentication token&gt;,&lt;item index&gt;,&lt;item type&gt;,&lt;bid value&gt;

Example: 3d962520-62db-4130-b854-87344a0640ca,0,t5,8.000000

####Step 4
Server responds with status of bid

1. OK if bid accepted
2. error details if bid not accepted

####Step 5
Client goes back to **Step 1**

####Step 6
Close connection, exit!
