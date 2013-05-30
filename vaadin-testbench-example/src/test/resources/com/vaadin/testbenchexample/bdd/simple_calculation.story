#
# This example shows a simple BDD test definition using JBehave.
# See http://jbehave.org for details.
#

Meta:

Narrative:
As a user
I want to perform calculations
So that I can easily get the results without calculating in my head

Scenario: Calculate 1+2
Given I have the calculator open
When I push 1+2
Then the display should show 3.0

Scenario: Calculate 1337*5/5
Given I have the calculator open
When I push 1337*5/5
Then the display should show 1337.0

Scenario: Calculate 42-5000
Given I have the calculator open
When I push 42-5000
Then the display should show -4958.0