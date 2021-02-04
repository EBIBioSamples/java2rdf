echo -e "\n\n\tDownloading credentials\n"
gist_url="https://gist.githubusercontent.com/marco-brandizi/$SECRETS_GIST_TOKEN/raw/github-brandizi-secrets.sh"
source <(wget -O - "$gist_url")
