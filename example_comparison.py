#!/usr/bin/env python3
"""
Example usage script for Figma comparison tool

This script demonstrates how to use the Figma comparison tool with the provided file URLs.
Replace YOUR_FIGMA_TOKEN with your actual Figma API token.
"""

import os
import sys
import subprocess

# Figma file URLs from the requirements
FILE1_URL = "https://www.figma.com/design/bj0HKrEhnqolfdSVCk7dtp/Spanish-Design-Geidea-App-2025?node-id=791-21427&p=f&m=dev"
FILE2_URL = "https://www.figma.com/design/egUxaGuWWwJe0lnmsUvD6m/KSA-Geidea-Merchant-App--Content-?node-id=10426-41662&p=f&m=dev"

def main():
    # Check if token is provided as environment variable
    token = os.environ.get("FIGMA_TOKEN")
    
    if not token:
        print("Error: FIGMA_TOKEN environment variable not set")
        print("\nPlease set your Figma API token:")
        print("  export FIGMA_TOKEN='your_token_here'")
        print("\nOr pass it directly:")
        print(f"  python figma_comparison.py --token YOUR_TOKEN --file1 FILE1_URL --file2 FILE2_URL")
        sys.exit(1)
    
    print("=" * 60)
    print("Figma File Comparison - Geidea Designs")
    print("=" * 60)
    print("\nComparing:")
    print(f"  File 1: Spanish Design Geidea App 2025")
    print(f"  File 2: KSA Geidea Merchant App Content")
    print(f"\nOutput: geidea_comparison.xlsx")
    print("=" * 60)
    print()
    
    # Run the comparison tool
    cmd = [
        "python3",
        "figma_comparison.py",
        "--token", token,
        "--file1", FILE1_URL,
        "--file2", FILE2_URL,
        "--output", "geidea_comparison.xlsx"
    ]
    
    try:
        subprocess.run(cmd, check=True)
        print("\n" + "=" * 60)
        print("✓ Comparison completed successfully!")
        print("✓ Check 'geidea_comparison.xlsx' for the results")
        print("=" * 60)
    except subprocess.CalledProcessError as e:
        print(f"\n✗ Error running comparison: {e}")
        sys.exit(1)
    except FileNotFoundError:
        print("\n✗ Error: figma_comparison.py not found")
        print("Make sure you're running this script from the correct directory")
        sys.exit(1)

if __name__ == "__main__":
    main()
