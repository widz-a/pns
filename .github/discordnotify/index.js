const { SNAPSHOT_WEBHOOK, IMAGE_URL, GITHUB_TOKEN, ID } = process.env;
 
const [t0, t1, repo, branch, snapshotAddress, releaseAddress, compareURL, success] = process.argv;
const named = repo.split("/")[1];

const urlsplit = releaseAddress.split("/");
const idR = urlsplit[urlsplit.length - 1];

const urlsplit1 = snapshotAddress.split("/");
const idS = urlsplit1[urlsplit1.length - 1];

async function main() {
    if (SNAPSHOT_WEBHOOK == null || SNAPSHOT_WEBHOOK == "") {
        console.log(`SNAPSHOT_WEBHOOK doesnt exist.`);
        return;
    }
    
    let githubInfoArray = [
        `**Branch:** ${branch}`,
        `**Status:** ${(success == undefined ? "tbh idk" : (success ? "success" : "failure"))}`,
    ];

    let resp = await getCompare(compareURL)

    let changesArray = [ "\n**Changes:**" ];
    let hasChanges = false;

    if (resp.commits) {
        resp.commits.forEach(commit => {
            changesArray.push(`- [\`${commit.sha.substring(0, 7)}\`](https://github.com/${repo}/commit/${commit.sha}) *${commit.commit.message}*`)
            hasChanges = true;
        })
    }
    
    let description = githubInfoArray.concat(hasChanges ? changesArray : []);
    description.push(`\n**Info:** [${idS}](${snapshotAddress})`)
    description.push(`**Download:** [v${idR}](${releaseAddress})`)
    
    const webhook = {
        username: `${named} Builds`,
        content: "@everyone",
        embeds: [
            {
                title: `New ${named} build!`,
                description: description.join("\n"),
                url: `https://github.com/${repo}`,
                color: success ? 2672680 : 13117480
            }
        ]
    };
    
    console.log("Webhook: ", JSON.stringify(webhook, null, 2))
    
    fetch(SNAPSHOT_WEBHOOK, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(webhook)
    }).then(resp => {
        console.log("Status: ", resp.status)
        resp.text().then(a => console.log(a))
    });
}


async function getCompare(url) {
    const resp = await fetch(url, { method: "GET", headers: { Authorization: `token ${GITHUB_TOKEN}` }})
    return await resp.json();
}

main()
