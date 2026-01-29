# Figma File Comparison Tool

A Python tool to compare two Figma design files and generate an Excel report with screen-by-screen comparisons, including images and comments.

## Features

- 🔍 Compare screens from two Figma files
- 📊 Export comparison to Excel format
- 🖼️ Include screen images in the report
- 💬 Extract and display comments from both files
- 📝 Side-by-side comparison of matching screens

## Prerequisites

- Python 3.7 or higher
- Figma API token ([Get one here](https://www.figma.com/developers/api#access-tokens))

## Installation

1. Install Python dependencies:

```bash
pip install -r requirements.txt
```

## Usage

### Basic Usage

```bash
python figma_comparison.py \
  --token YOUR_FIGMA_API_TOKEN \
  --file1 "https://www.figma.com/design/bj0HKrEhnqolfdSVCk7dtp/Spanish-Design-Geidea-App-2025?node-id=791-21427&p=f&m=dev" \
  --file2 "https://www.figma.com/design/egUxaGuWWwJe0lnmsUvD6m/KSA-Geidea-Merchant-App--Content-?node-id=10426-41662&p=f&m=dev" \
  --output comparison_report.xlsx
```

### Command Line Arguments

- `--token` (required): Your Figma API personal access token
- `--file1` (required): First Figma file URL or file key
- `--file2` (required): Second Figma file URL or file key
- `--output` (optional): Output Excel filename (default: `figma_comparison.xlsx`)

### Getting a Figma API Token

1. Go to [Figma Settings](https://www.figma.com/settings)
2. Scroll down to "Personal access tokens"
3. Click "Create new token"
4. Give it a name (e.g., "File Comparison")
5. Copy the token and use it with `--token`

## Output

The tool generates:

1. **Excel file** (`figma_comparison.xlsx` or your specified name):
   - Screen names from both files
   - Side-by-side images of each screen
   - Comments associated with each screen
   - Color-coded headers for easy reading

2. **Images folder** (`figma_comparison_images/`):
   - All screen images downloaded from both Figma files
   - Named by file and node ID for reference

## Excel Report Structure

| Screen Name | File 1 Image | Screen Name | File 2 Image | Comments |
|-------------|--------------|-------------|--------------|----------|
| Home Screen | [Image]      | Home Screen | [Image]      | File 1: Fix button alignment<br>File 2: Update colors |
| Login       | [Image]      | Login       | [Image]      | - |

## Example

Compare the Spanish and KSA Geidea designs:

```bash
# Set your token as an environment variable (recommended)
export FIGMA_TOKEN="your_token_here"

# Run the comparison
python figma_comparison.py \
  --token $FIGMA_TOKEN \
  --file1 "https://www.figma.com/design/bj0HKrEhnqolfdSVCk7dtp/Spanish-Design-Geidea-App-2025?node-id=791-21427&p=f&m=dev" \
  --file2 "https://www.figma.com/design/egUxaGuWWwJe0lnmsUvD6m/KSA-Geidea-Merchant-App--Content-?node-id=10426-41662&p=f&m=dev" \
  --output geidea_comparison.xlsx
```

## How It Works

1. **Authentication**: Uses your Figma API token to access file data
2. **File Fetching**: Downloads file structure and metadata from both Figma files
3. **Screen Extraction**: Identifies all frames, components, and screens in each file
4. **Image Generation**: Requests PNG exports of each screen from Figma
5. **Comment Collection**: Fetches all comments associated with screens
6. **Comparison**: Matches screens by name between the two files
7. **Excel Generation**: Creates a formatted Excel report with embedded images

## Troubleshooting

### Error: "Could not fetch file data"
- Check that your Figma API token is valid
- Ensure you have access to both Figma files
- Verify the file URLs or keys are correct

### Error: "Image not available"
- Some screens may not be exportable
- Check your Figma API rate limits
- Ensure the screens are properly named frames

### Rate Limits
- Figma API has rate limits (typically 60 requests per minute)
- The tool automatically batches image requests to minimize API calls
- For very large files, you may need to wait between runs

## Limitations

- Only compares screens with matching names
- Requires read access to both Figma files
- Images are exported at 2x scale and then resized for Excel
- Large files with many screens may take several minutes to process

## Security Note

⚠️ **Never commit your Figma API token to version control!**

Always use environment variables or secure methods to pass your token:

```bash
export FIGMA_TOKEN="your_token_here"
python figma_comparison.py --token $FIGMA_TOKEN --file1 URL1 --file2 URL2
```

## Contributing

Feel free to submit issues or pull requests to improve this tool!

## License

This tool is provided as-is for comparing Figma design files.
