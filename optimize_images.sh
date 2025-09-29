#!/bin/bash
# AllWorkouts Image Optimization Reference Script
# Uses ImageMagick to optimize PNG images for Android apps

echo "AllWorkouts Image Optimization Script"
echo "===================================="
echo ""
echo "This script shows the ImageMagick commands used to optimize"
echo "the Back Strengthening workout images from ~5.8MB to ~103KB"
echo ""
echo "Commands used (resize to 256x256px + quality optimization):"
echo ""

# Example commands (commented out - for reference only)
# magick input.png -resize 256x256 -strip -quality 90 output.png

echo "# For each Back Strengthening image:"
echo "magick bridges.png -resize 256x256 -strip -quality 90 bridges_optimized.png"
echo "magick side_lying_hip_abduction.png -resize 256x256 -strip -quality 90 side_lying_hip_abduction_optimized.png"
echo "magick side_plan_on_knees.png -resize 256x256 -strip -quality 90 side_plan_on_knees_optimized.png"
echo "magick standing_trunk_rotations_with_resistance.png -resize 256x256 -strip -quality 90 standing_trunk_rotations_with_resistance_optimized.png"
echo "magick straight_leg_raise.png -resize 256x256 -strip -quality 90 straight_leg_raise_optimized.png"
echo ""
echo "Parameters explained:"
echo "  -resize 256x256    : Resize to 256x256 pixels (good for mobile app illustrations)"
echo "  -strip             : Remove metadata to reduce file size"
echo "  -quality 90        : PNG compression quality (90 is high quality with good compression)"
echo ""
echo "Alternative sizes for different use cases:"
echo "  -resize 128x128    : For smaller icons"
echo "  -resize 512x512    : For larger illustrations"
echo "  -resize 64x64      : For tiny icons"
echo ""
echo "Results achieved:"
echo "  • 98.2% file size reduction (5.8MB → 103KB total)"
echo "  • Maintained visual quality at 256x256 resolution"
echo "  • Perfect for Android mobile app usage"
echo ""
echo "Original images backed up in: drawable/original_images_backup/"