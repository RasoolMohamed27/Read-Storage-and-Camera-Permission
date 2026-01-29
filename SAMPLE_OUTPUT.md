# Sample Output Description

## What the Excel Report Contains

When you run the Figma comparison tool, you'll get an Excel file that looks like this:

### Sheet: "Figma Comparison"

#### Header Row (Blue Background)
- **Column A**: Screen Name (from File 1)
- **Column B**: [File 1 Name] Image
- **Column C**: Screen Name (from File 2)
- **Column D**: [File 2 Name] Image
- **Column E**: Comments

#### Data Rows (One per Screen)

Each row represents a screen that exists in one or both files:

**Example Row 1 - Screen in Both Files:**
```
| Home Screen | [Image 250x250px] | Home Screen | [Image 250x250px] | File 1: Update button color
                                                                    File 2: Fix spacing |
```

**Example Row 2 - Screen Only in File 1:**
```
| Login Screen | [Image 250x250px] | | | File 1: New screen not in v2 |
```

**Example Row 3 - Screen Only in File 2:**
```
| | | Dashboard | [Image 250x250px] | File 2: Added in new version |
```

### Image Specifications

- **Format**: PNG
- **Size**: 250x250 pixels (scaled to fit in Excel)
- **Quality**: 2x scale from Figma (high quality)
- **Aspect Ratio**: Preserved (may not fill entire cell)

### Comments Format

Comments are formatted as:
```
File 1: [Comment text from first file]
File 2: [Comment text from second file]
```

Multiple comments from the same file are listed separately.

## Example: Geidea Design Comparison

For the Spanish and KSA Geidea designs, you might see:

### Header
```
| Screen Name | Spanish Design Geidea App 2025 Image | Screen Name | KSA Geidea Merchant App Content Image | Comments |
```

### Sample Rows
```
| Home | [Spanish Home Screen] | Home | [KSA Home Screen] | File 1: Spanish text labels
                                                          File 2: Arabic RTL layout |

| Login | [Spanish Login] | Login | [KSA Login] | File 1: Email input
                                                File 2: Phone number input |

| Payment | [Spanish Payment] | Payment | [KSA Payment] | File 1: Euro currency
                                                        File 2: SAR currency |
```

## Folder Structure

After running the tool, you'll have:

```
your-directory/
├── geidea_comparison.xlsx          # Main Excel report
└── figma_comparison_images/        # All exported images
    ├── file1_791-21427.png
    ├── file1_791-21428.png
    ├── file2_10426-41662.png
    ├── file2_10426-41663.png
    └── ...
```

## File Sizes

Typical file sizes:
- **Excel file**: 5-50 MB (depending on number of screens)
- **Each image**: 50-500 KB
- **Images folder**: May be larger than Excel file
- **Total**: Plan for 50-200 MB for typical comparison

## Opening the Report

### Excel (Desktop)
- Full functionality
- Images display properly
- Can adjust row heights/column widths
- Can add notes and annotations

### Google Sheets
- Upload the Excel file
- Images may need to be re-embedded
- Most formatting preserved
- Online collaboration supported

### LibreOffice Calc
- Free alternative to Excel
- Opens .xlsx files
- Full image support
- Cross-platform (Windows/Mac/Linux)

## Using the Report

### Review Process
1. Open the Excel file
2. Scroll through each row
3. Compare images side-by-side
4. Read comments for context
5. Add your own notes in new columns

### Collaboration
1. Share Excel file via email/cloud
2. Team members can add comments
3. Use Excel review features
4. Export to PDF for stakeholders

### Filtering
- Use Excel filters on Screen Name columns
- Sort by screen name alphabetically
- Filter to show only screens with comments
- Create custom views for different reviews

## Tips for Best Results

1. **Screen Names**: Use consistent naming in both Figma files
2. **Organization**: Group related screens together
3. **Comments**: Add context in Figma before exporting
4. **Updates**: Re-run comparison after Figma changes
5. **Archiving**: Save dated versions (e.g., comparison_2025-01-29.xlsx)

## Customization Ideas

You can manually enhance the Excel report:

1. **Add Columns**:
   - Status (Approved/Needs Changes)
   - Assignee
   - Priority
   - Notes

2. **Conditional Formatting**:
   - Highlight screens with comments
   - Color-code by status
   - Mark screens only in one file

3. **Charts**:
   - Count screens per file
   - Track comment density
   - Show completion status

4. **Pivot Tables**:
   - Summarize by screen type
   - Group by feature area
   - Analysis by designer

## Sample Use Cases

### Design Review
- Present to stakeholders
- Show evolution between versions
- Document design decisions

### Localization Check
- Compare language versions
- Verify layout for different locales
- Ensure consistent design across markets

### A/B Testing
- Compare different design approaches
- Document test variants
- Share with research team

### Handoff to Developers
- Show final approved designs
- Include implementation notes
- Reference for both platforms

### Archive/Documentation
- Historical design record
- Version comparison
- Design system evolution

## Next Steps

After generating your report:

1. ✅ Review all screens
2. ✅ Note any discrepancies
3. ✅ Share with team
4. ✅ Document decisions
5. ✅ Update Figma as needed
6. ✅ Re-run comparison to verify
