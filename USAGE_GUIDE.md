# Figma Comparison Tool - Usage Guide

## Overview

This tool helps you compare two Figma design files and generate a comprehensive Excel report showing:
- All screens from both files
- Side-by-side comparison of matching screens
- High-quality images of each screen
- Comments and annotations from both files

## Setup

### 1. Install Dependencies

```bash
pip install -r requirements.txt
```

This will install:
- `requests` - For Figma API calls
- `openpyxl` - For Excel file generation
- `Pillow` - For image processing

### 2. Get Your Figma API Token

1. Go to your [Figma Settings](https://www.figma.com/settings)
2. Scroll to "Personal access tokens"
3. Click "Create new token"
4. Name it (e.g., "File Comparison Tool")
5. Copy the token immediately (you won't see it again!)

**Security Note:** Keep your token secret and never commit it to git!

## Basic Usage

### Command Line

```bash
python figma_comparison.py \
  --token YOUR_FIGMA_TOKEN \
  --file1 "FIRST_FIGMA_URL" \
  --file2 "SECOND_FIGMA_URL" \
  --output comparison_report.xlsx
```

### Using Environment Variables (Recommended)

Set your token as an environment variable for security:

```bash
# Linux/Mac
export FIGMA_TOKEN="your_token_here"

# Windows (Command Prompt)
set FIGMA_TOKEN=your_token_here

# Windows (PowerShell)
$env:FIGMA_TOKEN="your_token_here"
```

Then run:

```bash
python figma_comparison.py \
  --token $FIGMA_TOKEN \
  --file1 "FIRST_FIGMA_URL" \
  --file2 "SECOND_FIGMA_URL"
```

## Example: Comparing Geidea Designs

For the Spanish and KSA Geidea designs mentioned in the requirements:

```bash
export FIGMA_TOKEN="your_token_here"

python figma_comparison.py \
  --token $FIGMA_TOKEN \
  --file1 "https://www.figma.com/design/bj0HKrEhnqolfdSVCk7dtp/Spanish-Design-Geidea-App-2025?node-id=791-21427&p=f&m=dev" \
  --file2 "https://www.figma.com/design/egUxaGuWWwJe0lnmsUvD6m/KSA-Geidea-Merchant-App--Content-?node-id=10426-41662&p=f&m=dev" \
  --output geidea_comparison.xlsx
```

Or use the example script:

```bash
export FIGMA_TOKEN="your_token_here"
python example_comparison.py
```

## Understanding the Output

### Excel Report Structure

The generated Excel file contains:

| Column | Description |
|--------|-------------|
| Screen Name (Col A) | Name of the screen from File 1 |
| File 1 Image (Col B) | Screenshot from the first Figma file |
| Screen Name (Col C) | Name of the screen from File 2 |
| File 2 Image (Col D) | Screenshot from the second Figma file |
| Comments (Col E) | All comments from both files for these screens |

### Images Folder

All downloaded images are saved in `figma_comparison_images/` directory:
- Named by file and node ID for reference
- Kept as PNG files at 2x scale
- Can be used separately if needed

## Command Line Options

```
--token TOKEN     (Required) Your Figma API personal access token
--file1 FILE1     (Required) First Figma file URL or file key
--file2 FILE2     (Required) Second Figma file URL or file key
--output OUTPUT   (Optional) Output filename (default: figma_comparison.xlsx)
```

### File Input Formats

You can provide files in multiple formats:

1. **Full URL:**
   ```
   https://www.figma.com/design/FILE_KEY/File-Name?node-id=123
   ```

2. **File URL:**
   ```
   https://www.figma.com/file/FILE_KEY/File-Name
   ```

3. **File Key Only:**
   ```
   bj0HKrEhnqolfdSVCk7dtp
   ```

## What Gets Compared

The tool compares:
- **Frames** - Main design frames
- **Components** - Reusable component designs
- **Component Sets** - Variant component groups

Screens are matched by **name** across the two files.

## Comments Handling

Comments are extracted and displayed in the Excel report:
- Shows which file each comment is from
- Grouped by the screen they're attached to
- Includes all comment messages

## Troubleshooting

### "Could not fetch file data"

**Causes:**
- Invalid or expired Figma API token
- No access to the Figma file
- Incorrect file URL or key
- Network connectivity issues

**Solutions:**
1. Verify your token is valid: https://www.figma.com/settings
2. Ensure you have at least "can view" access to both files
3. Check the file URLs are correct
4. Try accessing the files in your browser first

### "Image not available"

**Causes:**
- Figma API rate limits
- Network timeout
- Some screen types may not be exportable

**Solutions:**
1. Wait a few minutes and try again (rate limits)
2. Check your internet connection
3. Some images may legitimately not be available

### Rate Limiting

Figma API limits:
- **60 requests per minute** per token
- The tool automatically batches image requests
- For large files (>100 screens), processing may take several minutes

If you hit rate limits:
1. Wait 1 minute before retrying
2. Consider comparing files with fewer screens first
3. Use a more specific node-id in the URL to limit scope

### Memory Issues

For very large files:
- Close other applications
- Process files in smaller batches
- Consider upgrading system RAM

## Advanced Usage

### Comparing Specific Pages

To compare only specific pages, use the node-id parameter in your URLs:

```bash
python figma_comparison.py \
  --token $FIGMA_TOKEN \
  --file1 "https://www.figma.com/design/FILE1?node-id=PAGE_ID" \
  --file2 "https://www.figma.com/design/FILE2?node-id=PAGE_ID"
```

### Batch Processing

Create a script to compare multiple file pairs:

```bash
#!/bin/bash
export FIGMA_TOKEN="your_token"

python figma_comparison.py --token $FIGMA_TOKEN \
  --file1 "FILE1_URL" --file2 "FILE2_URL" --output "comparison1.xlsx"

python figma_comparison.py --token $FIGMA_TOKEN \
  --file1 "FILE3_URL" --file2 "FILE4_URL" --output "comparison2.xlsx"
```

## Testing

Run the unit tests to verify everything works:

```bash
python test_figma_comparison.py
```

Expected output:
```
Ran 9 tests in 0.002s
OK
```

## Performance Tips

1. **Use specific node-ids** in URLs to limit scope
2. **Compare during off-peak hours** to avoid rate limits
3. **Process large files in batches** by page/section
4. **Keep designs organized** with clear screen names
5. **Close unnecessary applications** when processing large files

## Best Practices

1. **Name your screens consistently** across both files for better matching
2. **Use descriptive names** like "Home Screen - iOS" vs "Screen 1"
3. **Organize screens by feature** or user flow
4. **Add meaningful comments** in Figma for better reports
5. **Keep your token secure** - use environment variables

## Example Workflow

1. **Setup:**
   ```bash
   pip install -r requirements.txt
   export FIGMA_TOKEN="your_token"
   ```

2. **Run Comparison:**
   ```bash
   python figma_comparison.py \
     --token $FIGMA_TOKEN \
     --file1 "DESIGN_V1_URL" \
     --file2 "DESIGN_V2_URL" \
     --output "design_comparison_v1_v2.xlsx"
   ```

3. **Review Output:**
   - Open `design_comparison_v1_v2.xlsx` in Excel/Google Sheets
   - Review side-by-side screen comparisons
   - Check comments for design decisions
   - Share with team for feedback

4. **Cleanup (optional):**
   ```bash
   rm -rf figma_comparison_images/
   ```

## Support

For issues or questions:
1. Check this guide first
2. Review the main [README](FIGMA_COMPARISON_README.md)
3. Check Figma API documentation: https://www.figma.com/developers/api
4. Open an issue on the repository

## Additional Resources

- [Figma API Documentation](https://www.figma.com/developers/api)
- [Figma API Authentication](https://www.figma.com/developers/api#authentication)
- [OpenPyXL Documentation](https://openpyxl.readthedocs.io/)
