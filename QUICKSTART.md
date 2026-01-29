# Quick Start Guide - Figma Comparison Tool

## 🚀 Quick Start (3 Steps)

### Step 1: Install Dependencies
```bash
pip install -r requirements.txt
```

### Step 2: Get Your Figma Token
Visit: https://www.figma.com/settings → Personal access tokens → Create new token

### Step 3: Run Comparison
```bash
python figma_comparison.py \
  --token YOUR_FIGMA_TOKEN \
  --file1 "https://www.figma.com/design/FIRST_FILE_ID/..." \
  --file2 "https://www.figma.com/design/SECOND_FILE_ID/..." \
  --output comparison.xlsx
```

## 📊 What You Get

The tool generates:
1. **Excel file** with side-by-side screen comparisons
2. **Images folder** with all exported screenshots
3. **Comments** from both files included in the report

## 🎯 Your Specific Use Case

For comparing the Spanish and KSA Geidea designs:

```bash
# Set your token (do this once)
export FIGMA_TOKEN="your_token_here"

# Run the comparison
python figma_comparison.py \
  --token $FIGMA_TOKEN \
  --file1 "https://www.figma.com/design/bj0HKrEhnqolfdSVCk7dtp/Spanish-Design-Geidea-App-2025?node-id=791-21427&p=f&m=dev" \
  --file2 "https://www.figma.com/design/egUxaGuWWwJe0lnmsUvD6m/KSA-Geidea-Merchant-App--Content-?node-id=10426-41662&p=f&m=dev" \
  --output geidea_comparison.xlsx
```

Or simply:
```bash
export FIGMA_TOKEN="your_token_here"
python example_comparison.py
```

## 📖 Excel Report Preview

| Screen Name | Spanish Design Image | Screen Name | KSA Design Image | Comments |
|-------------|---------------------|-------------|------------------|----------|
| Home        | [Screenshot]        | Home        | [Screenshot]     | Comments... |
| Login       | [Screenshot]        | Login       | [Screenshot]     | Comments... |
| Dashboard   | [Screenshot]        | Dashboard   | [Screenshot]     | Comments... |

## 🔍 How It Works

```
1. Connect to Figma API
         ↓
2. Fetch both file structures
         ↓
3. Extract all screens/frames
         ↓
4. Download screen images
         ↓
5. Collect comments
         ↓
6. Match screens by name
         ↓
7. Generate Excel report
```

## ⚠️ Important Notes

- **Token Security**: Never commit your token to git!
- **File Access**: You need "can view" access to both Figma files
- **Rate Limits**: Figma API allows 60 requests/minute
- **Processing Time**: Large files may take 2-5 minutes

## 📚 More Documentation

- **[FIGMA_COMPARISON_README.md](FIGMA_COMPARISON_README.md)** - Full documentation
- **[USAGE_GUIDE.md](USAGE_GUIDE.md)** - Detailed usage and troubleshooting

## ✅ Verify Installation

Test that everything is working:
```bash
python test_figma_comparison.py
```

Expected output:
```
Ran 9 tests in 0.002s
OK
```

## 💡 Tips

1. Use descriptive screen names in Figma for better matching
2. Organize screens consistently across both files
3. Add comments in Figma for context in the report
4. Save your token as an environment variable for security

## 🆘 Common Issues

**"Module not found"**
```bash
pip install -r requirements.txt
```

**"Could not fetch file data"**
- Verify token is valid
- Check you have access to both files
- Test file URLs in browser first

**"Rate limit exceeded"**
- Wait 1 minute
- Try again with fewer screens

## 🎉 You're Ready!

Run the comparison and open the generated Excel file to see your side-by-side design comparison!
