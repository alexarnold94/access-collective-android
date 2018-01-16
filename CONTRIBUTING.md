# Contributing

Here is an overview of how to contribute to the project - it's a bit long, but hopefully it will be useful!

## Tasks

All the projects requirements (and bugs) will be tracked in the form of tasks [on the Trello board](https://trello.com/b/UeOArXZa/project-specification). This will provide everyone with a simple overview of how the project is tracking, who's doing what, and what still needs to be done.

### Task Creation

For the most part, tasks will be created by the Project Manager or the Team Leader. However, if you find a bug, or realise that a requested feature is not on any of the lists, feel free to add it! There are just a few steps in adding tasks:

1. Create the task on [Trello](https://trello.com/b/UeOArXZa/project-specification)
 - Add any labels that seem appropriate
2. Create an issue in GitHub ([iOS](https://github.com/alexarnold94/access-collective-ios) and/or [Android](https://github.com/alexarnold94/access-collective-android))
 - If the feature or bug exists across both versions of the app, then create an issue in both repos
3. Notify everyone on Facebook/Slack

### Task Assignment

Tasks will usually be self-assigned - if you see something you want to do, and there's nobody else doing it, go ahead! Just make sure you assign yourself to the task on Trello and to the issue on GitHub so that we don't have everyone doing the same task!

If you assign a task to someone else, let them know through Slack or Facebook.

## Development Process

This project will follow the [GitHub Flow](https://guides.github.com/introduction/flow/) development process. Basically, this keeps master clean and allows us to deploy regularly. The steps are as follows:

0. Move assigned task from `Tasks to do` to `Doing` in Trello.
1. Branch from `master`.
 - Give your branch a unique, relevant name e.g. `list-maps` or `bugfix-issue-12`.
2. Code away!
3. If you complete the feature or fix the bug, create a pull request in GitHub.
 - Select `master` as the base and your branch as the comparison branch.
 - Add a name and describe what you have done in the comment section.
 - Include a reference to the issue(s) that this request deals with e.g. `Closes #13`. GitHub is smart and it will auto-close the issue once the pull request is approved.
 - Add review(s) and labels.
 - __Note:__ You can always make a pull request if you get stuck - the other feedback and help from reviewers.
4. Wait for feedback and approval.
 - The reviewers will give you feedback, commenting on your code. You may have to make changes based on this feedback.
 - Once the code looks good, a reviewer can approve it.
5. Merge pull request.
 - Once approval has been given, the branch can be merged into `master`.
 - __Note:__ Since we won't be doing any unit testing, the code in a pull request should be __tested on a physical device__ before approval.
6. Delete merged branch.
7. Move task from `Doing` to `Done` in Trello.
