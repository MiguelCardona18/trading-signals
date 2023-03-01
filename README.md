# trading-signals
The aim of this project is to create a machine learning algorithm, based on Genetic Computing, that maximises profit by deciding
to BUY, SELL, or HOLD stock options - given a number of trading signals. In this case we used Unilever's stock price, and the following signals:
<ul>
<li>EMA (12-26)</li>
<li>TBR</li>
<li>Vol</li>
<li>MOM</li>
</ul>

From Unilever's stock price we calculated the output of the trading signals on Excel. This file was then fed to the Genetic Algorithm (GA), which randomly
allocated a weight from 0 to 1 to each trading signal. In each iteration the GA would decide to BUY, SELL or HOLD based on the trading signals and their
respective weights. 

For example, whem EMA = 1, TBR = 1, Vol = 3, and MOM = 2
and EMA says BUY, TBR says BUY, Vol says SELL and MOM says HOLD the total balance is the following: BUY = (1+1) = 2, SELL = 3, and HOLD = 2, so the
algorithm decides to SELL. This is the case for the mentioned weights, this would be different when these are changed. 

After many iterations with different weight combinations the GA outputs the combination that has provided the most profit out of the ones it has tried.
The post-implementation analysis is provided in the PowerPoint file.
